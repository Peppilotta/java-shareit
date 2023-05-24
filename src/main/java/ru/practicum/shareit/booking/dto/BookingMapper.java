package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Objects;

@Component
@AllArgsConstructor
public class BookingMapper {

    private final UserService userService;

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    public Booking toBooking(BookingDtoWithId bookingDto, Long userId) {
        Long itemId = bookingDto.getItemId();
        BookingStatus status = BookingStatus.WAITING;
        if (!Objects.equals(bookingDto.getStatus(), null)) {
            status = BookingStatus.valueOf(bookingDto.getStatus());
        }
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(status)
                .item(itemMapper.toItem(itemService.getItem(userId, itemId)))
                .booker(userMapper.toUser(userService.getUser(userId)))
                .build();
    }

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(itemMapper.toDto(booking.getItem()))
                .booker(userMapper.toDto(booking.getBooker()))
                .build();
    }
}
