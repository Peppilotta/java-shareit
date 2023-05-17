package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingDtoWithId bookingDtoWithId);

    BookingDtoWithId toDtoWithId(Booking booking);

    BookingDto toDto(Booking booking);
}