package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking save(Booking booking, Long userId);

    Booking changeBookingStatus(Long bookingId, Boolean isApproved, Long ownerId);

    Booking getBooking(Long requesterId, Long bookingId);

    List<BookingDto> getBookingByState(Long ownerId, String state);

    List<BookingDto> getBookingByStateAndOwner(Long ownerId, String state);
}