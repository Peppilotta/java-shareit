package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDto create(@Valid @RequestBody BookingDtoWithId bookingCreateDto,
                             @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.save(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable long bookingId, @RequestParam Boolean approved,
                             @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.changeBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping("")
    public List<BookingDto> getBookingByState(@RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBookingByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsByStateAndOwner
            (@RequestParam(defaultValue = "ALL", required = false) String state,
             @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBookingByStateAndOwner(userId, state);
    }
}