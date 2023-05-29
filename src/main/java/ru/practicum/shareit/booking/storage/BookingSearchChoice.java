package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSearchType;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookingSearchChoice {

    static Map<BookingSearchType, BookingSearch> searchMap = new HashMap<>();

    static {
        searchMap.put(BookingSearchType.ALL, new SearchAll());
        searchMap.put(BookingSearchType.CURRENT, new SearchCurrent());
        searchMap.put(BookingSearchType.PAST, new SearchPast());
        searchMap.put(BookingSearchType.FUTURE, new SearchFuture());
        searchMap.put(BookingSearchType.WAITING, new SearchWaiting());
        searchMap.put(BookingSearchType.REJECTED, new SearchRejected());
    }

    public static Optional<BookingSearch> getSearchMethod(BookingSearchType type) {
        return Optional.ofNullable(searchMap.get(type));
    }
}

class SearchAll implements BookingSearch {

    @Override
    public List<Booking> getBookings(long ownerId, BookingRepository bookingRepository) {
        return bookingRepository.searchByBooker(ownerId);
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