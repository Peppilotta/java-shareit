package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @Positive(message = "ItemId must be positive")
    private long itemId;

    @FutureOrPresent(message = "Booking start must be in the future")
    private LocalDateTime start;

    @Future(message = "Booking end must be in the future")
    private LocalDateTime end;
}