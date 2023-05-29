package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder
public class BookingDtoWithId implements Serializable {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String status;
    private final Long itemId;
    private final Long userId;
}