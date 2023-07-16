package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class MyApplicationException extends RuntimeException {

    private HttpStatus httpStatus;
    private String errorMessage;

    public MyApplicationException(String errorMessage, HttpStatus httpStatus) {
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
