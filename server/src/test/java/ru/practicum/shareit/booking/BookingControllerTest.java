package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBooking_StandardBehavior() {
        ReflectionTestUtils.setField(bookingController, "bookingService", bookingService);

        BookingDto bookingDto = createBookingDto();
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        BookingDto expected = bookingController.getBookingById(33L, 33L);
        assertThat((expected.getId()), equalTo(bookingDto.getId()));
        assertThat((expected.getBooker()), equalTo(bookingDto.getBooker()));
        assertThat(expected.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat((expected.getItem()), equalTo(bookingDto.getItem()));
    }

    @Test
    void getBookingByState_ShouldReturnList() throws Exception {
        ReflectionTestUtils.setField(bookingController, "bookingService", bookingService);

        BookingDto bookingDto = createBookingDto();
        List<BookingDto> expectedItems = List.of(bookingDto);
        when(bookingService.getBookingByState(anyLong(), any(), any())).thenReturn(expectedItems);
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .param("from", "3")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].booker.id").value(equalTo(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id").value(equalTo(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", equalTo(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].id").value(equalTo(bookingDto.getId()), Long.class));
    }

    @Test
    void create_StandardBehavior() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookingService.save(any(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booker.id").value(equalTo(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id").value(equalTo(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", equalTo(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.id").value(equalTo(bookingDto.getId()), Long.class));
    }

    @Test
    void update_StandardBehavior() throws Exception {
        BookingDto bookingDtoWithUpdates = createBookingDto();

        bookingDtoWithUpdates.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), any())).thenReturn(bookingDtoWithUpdates);

        mockMvc.perform(patch("/bookings/{bookingId}", 33L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booker.id").value(equalTo(bookingDtoWithUpdates.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id").value(equalTo(bookingDtoWithUpdates.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", equalTo(bookingDtoWithUpdates.getStatus().toString())))
                .andExpect(jsonPath("$.id").value(equalTo(bookingDtoWithUpdates.getId()), Long.class));
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(33L)
                .item(createItemDto())
                .booker(createUserDto())
                .start(dateTime.minusDays(5))
                .end(dateTime.plusDays(5))
                .status(BookingStatus.WAITING)
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(33L)
                .name("Alex")
                .email("azvarich@rubytech.ru")
                .build();
    }

    private UserDto createOwnerDto() {
        return UserDto.builder()
                .id(99L)
                .name("Tigran")
                .email("tigran@rubytech.ru")
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .owner(createOwnerDto())
                .requestId(100L)
                .build();
    }
}