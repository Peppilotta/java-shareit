package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSearchType;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingSearch {

    private final BookingRepository bookingRepository;

    public List<Booking> getBookings(long ownerId, BookingSearchType type) {
        switch (type) {
            case ALL:
                return bookingRepository.searchByBooker(ownerId);
            case PAST:
                return bookingRepository.searchByBookerInPastTime(ownerId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.searchByBookerInFutureTime(ownerId, LocalDateTime.now());
            case CURRENT:
                return bookingRepository.searchByBookerInPresentTime(ownerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.searchByBookerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.searchByBookerAndStatus(ownerId, BookingStatus.REJECTED);
            default:
                throw new BadRequestException("Unknown state");
        }
    }

    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingSearchType type) {
        switch (type) {
            case ALL:
                return bookingRepository.searchByItemOwner(ownerId);
            case PAST:
                return bookingRepository.searchByItemOwnerInPastTime(ownerId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.searchBookingsByItemOwnerInFutureTime(ownerId, LocalDateTime.now());
            case CURRENT:
                return bookingRepository.searchByItemOwnerInPresentTime(ownerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.searchByBookerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.searchByItemOwnerAndStatus(ownerId, BookingStatus.REJECTED);
            default:
                throw new BadRequestException("Unknown state");
        }
    }
}