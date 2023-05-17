package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingSearch {
    List<Booking> getBookings(long ownerId, BookingRepository bookingRepository);

    List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository);
}