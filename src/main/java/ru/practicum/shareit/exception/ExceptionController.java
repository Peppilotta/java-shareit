package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @ExceptionHandler(ItemDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongFilmId(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of(ERROR, "wrong id",
                MESSAGE, e.getMessage());
    }

    @ExceptionHandler(FindDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of(ERROR, "conflict",
                MESSAGE, e.getMessage());
    }

    @ExceptionHandler(NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNotOwner(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of(ERROR, "Forbidden",
                MESSAGE, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of(ERROR, "bad request",
                MESSAGE, e.getMessage());
    }
}