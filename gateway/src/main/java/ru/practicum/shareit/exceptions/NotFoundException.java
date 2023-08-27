package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends MyApplicationException {

    public NotFoundException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }
}
