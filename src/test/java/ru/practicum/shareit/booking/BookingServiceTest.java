package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    LocalDateTime dateTime = LocalDateTime.of(2023, 8, 22, 9, 0, 0, 0);

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookingService bookingService = new BookingService(bookingRepository,
            userRepository, itemRepository, bookingMapper, itemMapper, userMapper);

    @Test
    void save_StandardBehavior() {
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(bookingService, "bookingMapper", bookingMapper);
        ReflectionTestUtils.setField(bookingService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(bookingService, "userMapper", userMapper);

        User user = createUser();
        UserDto userDto = createUserDto();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        BookingDtoWithId bookingDtoWithId = createBookingDtoWithId();
        Booking booking = createBooking();
        BookingDto bookingDto = createBookingDto();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(userMapper.toDto(any())).thenReturn(userDto);
        when(itemMapper.toDto(any())).thenReturn(itemDto);
        when(bookingMapper.toBooking(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto expectedBookingDto = bookingService.save(bookingDtoWithId, user.getId());

        assertThat(expectedBookingDto.getId(), equalTo(bookingDtoWithId.getId()));
        assertThat(expectedBookingDto.getStart(), equalTo(bookingDtoWithId.getStart()));
        assertThat(expectedBookingDto.getEnd(), equalTo(bookingDtoWithId.getEnd()));
        assertThat(expectedBookingDto.getStatus().toString(), equalTo(bookingDtoWithId.getStatus()));
    }

    @Test
    void save_UserIdNotExist() {
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);

        Long userId = 100L;
        BookingDtoWithId bookingDtoWithId = createBookingDtoWithId();

        when(userRepository.existsById(anyLong())).thenReturn(false);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> bookingService.save(bookingDtoWithId, userId));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("User with id=" + userId + " not exists."));
    }

    @Test
    void save_ItemIdNotExist() {
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingMapper", bookingMapper);

        Long userId = 100L;
        Long itemId = 100L;
        Booking booking = createBooking();
        BookingDtoWithId bookingDtoWithId = createBookingDtoWithId();
        bookingDtoWithId.setItemId(itemId);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        when(bookingMapper.toBooking(any())).thenReturn(booking);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> bookingService.save(bookingDtoWithId, userId));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("Item with id=" + itemId + " not exists."));
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(33L)
                .booker(createUser())
                .start(dateTime.plusDays(5))
                .end(dateTime.plusDays(10))
                .status(BookingStatus.WAITING)
                .item(createItem())
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(33L)
                .booker(createUserDto())
                .start(dateTime.plusDays(5))
                .end(dateTime.plusDays(10))
                .status(BookingStatus.WAITING)
                .item(createItemDto())
                .build();
    }

    private BookingDtoWithId createBookingDtoWithId() {
        return BookingDtoWithId.builder()
                .id(33L)
                .userId(createUserDto().getId())
                .start(dateTime.plusDays(5))
                .end(dateTime.plusDays(10))
                .status(BookingStatus.WAITING.toString())
                .itemId(createItemDto().getId())
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .requestId(100L)
                .owner(createOwner())
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .requestId(100L)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(33L)
                .name("Alex")
                .email("azvarich@rubytech.ru")
                .build();
    }

    private User createOwner() {
        return User.builder()
                .id(99L)
                .name("Tigran")
                .email("tigran@rubytech.ru")
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(33L)
                .name("Alex")
                .email("azvarich@rubytech.ru")
                .build();
    }
}