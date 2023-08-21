package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithProposalsDto;
import ru.practicum.shareit.request.service.RequestService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequest_StandardBehavior() {
        ReflectionTestUtils.setField(requestController, "requestService", requestService);

        RequestDto requestDto = createRequestDto();
        RequestWithProposalsDto requestWithProposalsDto = createRequestWithProposalsDto();
        when(requestService.getRequest(anyLong(), anyLong())).thenReturn(requestWithProposalsDto);

        RequestWithProposalsDto expected = requestController.getRequest(33L, 33L);
        assertThat((expected.getId()), equalTo(requestDto.getId()));
        assertThat((expected.getRequester()), equalTo(requestWithProposalsDto.getRequester()));
        assertThat(expected.getDescription(), equalTo(requestWithProposalsDto.getDescription()));
        assertThat((expected.getCreated()), equalTo(requestWithProposalsDto.getCreated()));
        assertThat((expected.getItems().get(0)), equalTo(requestWithProposalsDto.getItems().get(0)));
    }

    @Test
    void getRequests_ShouldReturnList() throws Exception {
        ReflectionTestUtils.setField(requestController, "requestService", requestService);
        RequestWithProposalsDto requestWithProposalsDto = createRequestWithProposalsDto();
        List<RequestWithProposalsDto> requests = List.of(requestWithProposalsDto);

        when(requestService.getRequests(anyLong())).thenReturn(requests);
        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name", equalTo(requestWithProposalsDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].id").value(equalTo(requestWithProposalsDto.getId()), Long.class));
    }

    @Test
    void getRequestsAll_ShouldReturnList() throws Exception {
        ReflectionTestUtils.setField(requestController, "requestService", requestService);
        RequestWithProposalsDto requestWithProposalsDto = createRequestWithProposalsDto();
        List<RequestWithProposalsDto> requests = List.of(requestWithProposalsDto);

        when(requestService.getPartOfRequests(anyLong(), any(), any())).thenReturn(requests);
        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .param("from", "3")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name", equalTo(requestWithProposalsDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].id").value(equalTo(requestWithProposalsDto.getId()), Long.class));
    }

    @Test
    void create_StandardBehavior() throws Exception {
        RequestDto requestDto = createRequestDto();
        when(requestService.save(anyLong(), any())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", equalTo(requestDto.getDescription())))
                .andExpect(jsonPath("$.id").value(equalTo(requestDto.getId()), Long.class));
    }

    private RequestDto createRequestDto() {
        return RequestDto.builder()
                .id(33L)
                .created(dateTime.minusDays(5))
                .requester(createUserDto())
                .description("нужна бетономешалка")
                .build();
    }

    private RequestWithProposalsDto createRequestWithProposalsDto() {
        RequestDto requestDto = createRequestDto();
        List<ItemDto> requestsDto = List.of(createItemDto());
        RequestWithProposalsDto requestWithProposalsDto = new RequestWithProposalsDto(requestDto.getId(),
                requestDto.getDescription(),
                requestDto.getRequester(),
                requestDto.getCreated());
        requestWithProposalsDto.setItems(requestsDto);
        return requestWithProposalsDto;
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