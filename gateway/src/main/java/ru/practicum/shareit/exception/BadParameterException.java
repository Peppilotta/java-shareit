package ru.practicum.shareit.exception;

public class BadParameterException extends IllegalArgumentException {
    public BadParameterException(String s) {
        super(s);
    }
}
