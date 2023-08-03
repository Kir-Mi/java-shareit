package ru.practicum.shareit.booking.dto;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.ValidationException;

public enum BookingStateDto {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingStateDto fromString(String str) {
        try {
            return BookingStateDto.valueOf(str);
        } catch (Exception e) {
            String msg = String.format("Unknown state: %s", str);
            throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}