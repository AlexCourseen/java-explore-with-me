package ru.yandex.practicum.ewm.exception;

public class ConflictedDataException extends RuntimeException {
    public ConflictedDataException(String message) {
        super(message);
    }
}
