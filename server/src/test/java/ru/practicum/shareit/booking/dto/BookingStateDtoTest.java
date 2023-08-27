package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateDtoTest {

    @Test
    void fromString_ValidState_ReturnsEnumValue() {
        BookingStateDto state = BookingStateDto.fromString("ALL");
        assertEquals(BookingStateDto.ALL, state);
    }

    @Test
    void fromString_UnknownState_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                BookingStateDto.fromString("INVALID_STATE"));
    }
}
