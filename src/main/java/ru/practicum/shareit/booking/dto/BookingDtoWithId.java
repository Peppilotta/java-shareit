package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingDtoWithId {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String status;
    private final Long itemId;
    private final Long userId;
}