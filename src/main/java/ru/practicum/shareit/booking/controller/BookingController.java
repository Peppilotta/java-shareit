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
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
//@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping()
    public BookingDto create(@Valid @RequestBody BookingDtoWithId bookingCreateDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingMapper.toDto(bookingService.save(bookingMapper.toBooking(bookingCreateDto), userId));
    }

    @PatchMapping("{bookingId}")
    public BookingDto update(@PathVariable long bookingId, @RequestParam Boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingMapper.toDto(bookingService.changeBookingStatus(bookingId, approved, userId));
    }

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingMapper.toDto(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping("")
    public List<BookingDto> getBookingByState(@RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsByStateAndOwner
            (@RequestParam(defaultValue = "ALL", required = false) String state,
             @RequestHeader("X-Sharer-User-Id") long userId) {

        return bookingService.getBookingByStateAndOwner(userId, state);
    }
}