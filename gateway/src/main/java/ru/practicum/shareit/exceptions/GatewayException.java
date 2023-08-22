package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class GatewayException extends MyApplicationException {
    public GatewayException(final String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }
}
