package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ErrorResponse {
    @Getter
    String error;
}
