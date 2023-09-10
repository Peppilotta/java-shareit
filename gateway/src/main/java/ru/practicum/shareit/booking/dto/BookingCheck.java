package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadParameterException;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class BookingCheck {

    public void checkDto(BookItemRequestDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (Objects.isNull(start)) {
            throw new BadParameterException("Type booking start");
        }
        if (Objects.isNull(end)) {
            throw new BadParameterException("Type booking end");
        }
        if (Objects.equals(end, start)) {
            throw new BadParameterException("Booking end equals booking start");
        }
        if (end.isBefore(start)) {
            throw new BadParameterException("Booking end is before booking start");
        }
    }

    public void checkApproved(Boolean approved) {
        if (Objects.isNull(approved)) {
            throw new BadParameterException("Type booking approving");
        }
    }
}
