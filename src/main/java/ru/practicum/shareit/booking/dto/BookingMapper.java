package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

@Component
@AllArgsConstructor
public class BookingMapper {

    public Booking toBooking(BookingDtoWithId bookingDto) {
        BookingStatus status = bookingDto.getStatus() != null
                ? BookingStatus.valueOf(bookingDto.getStatus())
                : BookingStatus.WAITING;
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(status)
                .build();
    }

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
