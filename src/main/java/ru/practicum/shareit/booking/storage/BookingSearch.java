package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    public List<Booking> getBookings(long ownerId, BookingSearchType type, Pageable pageable) {
        switch (type) {
            case ALL:
                return bookingRepository
                        .searchByBooker(ownerId, pageable).getContent();
            case PAST:
                return bookingRepository
                        .searchByBookerInPastTime(ownerId, LocalDateTime.now(), pageable).getContent();
            case FUTURE:
                return bookingRepository
                        .searchByBookerInFutureTime(ownerId, LocalDateTime.now(), pageable).getContent();
            case CURRENT:
                return bookingRepository
                        .searchByBookerInPresentTime(ownerId, LocalDateTime.now(), pageable).getContent();
            case WAITING:
                return bookingRepository
                        .searchByBookerAndStatus(ownerId, BookingStatus.WAITING, pageable).getContent();
            case REJECTED:
                return bookingRepository
                        .searchByBookerAndStatus(ownerId, BookingStatus.REJECTED, pageable).getContent();
            default:
                throw new BadRequestException("Unknown state");
        }
    }

    public List<Booking> getBookingsByItemsOwner(long ownerId, BookingSearchType type, Pageable pageable) {
        switch (type) {
            case ALL:
                return bookingRepository
                        .searchByItemOwner(ownerId, pageable).getContent();
            case PAST:
                return bookingRepository
                        .searchByItemOwnerInPastTime(ownerId, LocalDateTime.now(), pageable).getContent();
            case FUTURE:
                return bookingRepository
                        .searchBookingsByItemOwnerInFutureTime(ownerId, LocalDateTime.now(), pageable).getContent();
            case CURRENT:
                return bookingRepository
                        .searchByItemOwnerInPresentTime(ownerId, LocalDateTime.now(), pageable).getContent();
            case WAITING:
                return bookingRepository
                        .searchByItemOwnerAndStatus(ownerId, BookingStatus.WAITING, pageable).getContent();
            case REJECTED:
                return bookingRepository
                        .searchByItemOwnerAndStatus(ownerId, BookingStatus.REJECTED, pageable).getContent();
            default:
                throw new BadRequestException("Unknown state");
        }
    }
}