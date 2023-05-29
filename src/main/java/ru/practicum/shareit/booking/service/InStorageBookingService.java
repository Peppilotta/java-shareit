package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSearchType;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingSearch;
import ru.practicum.shareit.booking.storage.BookingSearchChoice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class InStorageBookingService implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    @Override
    public Booking save(Booking booking, Long userId) {
        log.info("New request SAVE");
        checkBookingBasicConstraints(booking, userId);
        booking.setStatus(BookingStatus.WAITING);
        log.debug("Bookings for user id: {} saved: {}", userId, booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeBookingStatus(Long bookingId, Boolean isApproved, Long requesterId) {
        log.info("New request change booking status");
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ItemDoesNotExistException("Booking with id " + bookingId + " not found"));

        if (!Objects.equals(booking.getItem().getOwner().getId(), requesterId)) {
            throw new ItemDoesNotExistException("Booking status could be changed only by owner");
        }
        BookingStatus newStatus = Boolean.TRUE.equals(isApproved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus().equals(newStatus)) {
            throw new BadRequestException("Booking status has already been changed");
        } else {
            booking.setStatus(newStatus);
            bookingRepository.save(booking);
            log.debug("Bookings  id: {} change status to: {}", bookingId, newStatus);
            return booking;
        }
    }

    @Override
    public Booking getBooking(Long requesterId, Long bookingId) {
        log.info("New request get booking");
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ItemDoesNotExistException("Booking with id " + bookingId + " not found"));

        checkItemOwner(booking, requesterId);
        log.info("booking = {}", booking);
        return booking;
    }

    @Override
    public List<BookingDto> getBookingByState(Long ownerId, String state) {
        log.info("New request get booking by state");
        checkUserExists(ownerId);
        checkState(state);
        BookingSearch bookingSearch = BookingSearchChoice
                .getSearchMethod(BookingSearchType.valueOf(state))
                .orElseThrow(IllegalArgumentException::new);

        List<BookingDto> bookingDtos = bookingSearch
                .getBookings(ownerId, bookingRepository)
                .stream()
                .filter(Objects::nonNull).map(bookingMapper::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        log.debug("Bookings for owner id: {} and state: {} returned collection: {}", ownerId, state, bookingDtos);
        return bookingDtos;
    }

    @Override
    public List<BookingDto> getBookingByStateAndOwner(Long ownerId, String state) {
        log.info("New request get booking by state and owner");
        checkUserExists(ownerId);
        checkState(state);
        BookingSearch bookingSearch = BookingSearchChoice
                .getSearchMethod(BookingSearchType.valueOf(state))
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS"));

        List<BookingDto> collect = bookingSearch
                .getBookingsByItemsOwner(ownerId, bookingRepository)
                .stream()
                .filter(Objects::nonNull).map(bookingMapper::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        log.debug("Bookings for owner id: {} and state: {} returned collection: {}", ownerId, state, collect);
        return collect;
    }

    private void checkItemOwner(Booking booking, Long requesterId) {
        if (!Objects.equals(booking.getBooker().getId(), requesterId)
                && !Objects.equals(booking.getItem().getOwner().getId(), requesterId)) {
            throw new ItemDoesNotExistException("Booking could be retrieved only by items owner or booking author");
        }
    }

    private void checkBookingBasicConstraints(Booking booking, Long requesterId) {
        checkUserExists(requesterId);
        Long itemId = booking.getItem().getId();
        checkItemExists(itemId);
        Item item = itemRepository.findById(itemId).get();
        if (Objects.equals(booking.getStart(), null)) {
            throw new BadRequestException("Booking start should be not null");
        }
        if (Objects.equals(booking.getEnd(), null)) {
            throw new BadRequestException("Booking end should be not null");
        }
        if (Objects.equals(item.getOwner().getId(), requesterId)) {
            throw new ItemDoesNotExistException("Owner want to book his/ her Item");
        }
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (end.isBefore(start)
                || Objects.equals(start, end)
                || end.isBefore(LocalDateTime.now())
                || start.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Booking start should be less than End and not be in past");
        }
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("Booking can't be made to unavailable item");
        }

        List<Booking> bookings = bookingRepository.searchByItemIdAndStartAddEnd(item.getId(),
                booking.getStart(), booking.getEnd());

        if (bookings.stream().anyMatch(b -> b.getStatus().equals(BookingStatus.WAITING)
                || b.getStatus().equals(BookingStatus.APPROVED))) {
            throw new BadRequestException("Booking can't be made to one item more than one time");
        }
    }

    private void checkUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void checkItemExists(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemDoesNotExistException("Item with id=" + id + " not exists.");
        }
    }

    private void checkState(String state) {
        List<String> types = Arrays.stream(BookingSearchType.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        if (!types.contains(state)) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}