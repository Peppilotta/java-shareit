package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookingSearchFactory {

    static Map<String, BookingSearch> searchMap = new HashMap<>();

    static {
        searchMap.put("ALL", new SearchAll());
        searchMap.put("CURRENT", new SearchCurrent());
        searchMap.put("PAST", new SearchPast());
        searchMap.put("FUTURE", new SearchFuture());
        searchMap.put("WAITING", new SearchWaiting());
        searchMap.put("REJECTED", new SearchRejected());
    }

    public static Optional<BookingSearch> getSearchMethod(String operator) {
        return Optional.ofNullable(searchMap.get(operator));
    }
}

class SearchAll implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {
        List<Booking> bookings = bookingRepository.searchByBooker(ownerId);
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository) {

        return bookingRepository.searchByItemOwner(ownerId);
    }
}

class SearchCurrent implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByBookerInPresentTime(ownerId, LocalDateTime.now());
    }

    @Override
    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByItemOwnerInPresentTime(ownerId, LocalDateTime.now());
    }
}

class SearchPast implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {

        return bookingRepository.searchByBookerInPastTime(ownerId, LocalDateTime.now());
    }

    @Override
    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByItemOwnerInPastTime(ownerId, LocalDateTime.now());
    }
}

class SearchFuture implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByBookerInFutureTime(ownerId, LocalDateTime.now());
    }

    @Override
    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchBookingsByItemOwnerInFutureTime(ownerId, LocalDateTime.now());
    }
}

class SearchWaiting implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByBookerAndStatus(ownerId, BookingStatus.WAITING);
    }

    @Override
    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByItemOwnerAndStatus(ownerId, BookingStatus.WAITING);
    }
}

class SearchRejected implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByBookerAndStatus(ownerId, BookingStatus.REJECTED);
    }

    @Override
    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByItemOwnerAndStatus(ownerId, BookingStatus.REJECTED);
    }
}