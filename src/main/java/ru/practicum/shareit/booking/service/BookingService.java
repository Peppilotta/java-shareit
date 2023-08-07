package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSearchType;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.storage.BookingSearch;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    public BookingDto save(BookingDtoWithId bookingDto, Long userId) {
        log.info("New request SAVE");
        checkUserExists(userId);
        Booking booking = toBookingWithItemAndBooker(bookingDto, userId);
        checkBookingBasicConstraints(booking, userId);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Bookings for user id: {} saved: {}", userId, booking);
        return toDtoWithItemAndBooker(bookingRepository.save(booking));
    }

    public BookingDto changeBookingStatus(Long bookingId, Boolean isApproved, Long requesterId) {
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
        }
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        log.info("Bookings  id: {} change status to: {}", bookingId, newStatus);
        return toDtoWithItemAndBooker(booking);
    }

    public BookingDto getBooking(Long requesterId, Long bookingId) {
        log.info("New request get booking");
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ItemDoesNotExistException("Booking with id " + bookingId + " not found"));

        checkItemOwner(booking, requesterId);
        log.info("booking = {}", booking);
        return toDtoWithItemAndBooker(booking);
    }

    public List<BookingDto> getBookingByState(Long ownerId, String state,
                                              Optional<Integer> from, Optional<Integer> size) {
        log.info("New request get booking by state");
        checkFromAndSize(from, size);
        checkUserExists(ownerId);
        checkState(state);
        BookingSearchType type = BookingSearchType.valueOf(state);
        BookingSearch bookingSearch = new BookingSearch(bookingRepository);
        List<BookingDto> bookingDtos = bookingSearch
                .getBookings(ownerId, type)
                .stream()
                .map(this::toDtoWithItemAndBooker)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        if (from.isPresent()) {
            Integer fromExist = from.get();
            int totalItems = bookingDtos.size();
            int first = (int) (fromExist == 0 ? fromExist : --fromExist);
            if (size.isPresent()) {
                totalItems = size.get() + first -1;
            }
            log.info("First = {}  and last = {} ",first,totalItems);
            return bookingDtos.subList(first, totalItems);
        }
        log.info("Bookings for owner id: {} and state: {} returned collection: {}", ownerId, state, bookingDtos);
        return bookingDtos;
    }

    public List<BookingDto> getBookingByStateAndOwner(Long ownerId, String state,
                                                      Optional<Integer> from, Optional<Integer> size) {
        log.info("New request get booking by state and owner");
        checkUserExists(ownerId);
        checkState(state);
        checkFromAndSize(from, size);
        BookingSearchType type = BookingSearchType.valueOf(state);
        BookingSearch bookingSearch = new BookingSearch(bookingRepository);

        List<BookingDto> bookingDtos = bookingSearch
                .getBookingsByItemsOwner(ownerId, type)
                .stream()
                .map(this::toDtoWithItemAndBooker)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        if (from.isPresent()) {
            Integer fromExist = from.get();
            int totalItems = (int) (itemRepository.count() + 1);
            int first = (int) (fromExist >= 1 ? --fromExist : fromExist);
            if (size.isPresent()) {
                totalItems = size.get() + first - 1;
            }
            log.info("First = {}  and last = {} ",first,totalItems);
            return bookingDtos.subList(first, totalItems);
        }
        log.info("Bookings for owner id: {} and state: {} returned collection: {}", ownerId, state, bookingDtos);
        return bookingDtos;
    }

    private Booking toBookingWithItemAndBooker(BookingDtoWithId bookingDto, Long userId) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        Long itemId = bookingDto.getItemId();
        checkItemExists(itemId);
        booking.setItem(itemRepository.findById(itemId).get());
        booking.setBooker(userRepository.findById(userId).get());
        return booking;
    }

    private BookingDto toDtoWithItemAndBooker(Booking booking) {
        BookingDto bookingDto = bookingMapper.toDto(booking);
        bookingDto.setItem(itemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(userMapper.toDto(booking.getBooker()));
        return bookingDto;
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

    private void checkFromAndSize(Optional<Integer> from, Optional<Integer> size) {
        if (from.isPresent() && from.get() < 0) {
            throw new BadRequestException("Start position must be >= 0, not " + from);
        }
        if (size.isPresent() && size.get() <= 0) {
            throw new BadRequestException("Size must be >= 0, not " + size);
        }
    }
}