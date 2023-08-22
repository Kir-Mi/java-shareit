package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DateProcessorTest {

    @Test
    void toDate_ValidDate_ReturnsLocalDateTime() {
        String dateString = "2023-08-13 15:30:00";
        LocalDateTime expectedDateTime = LocalDateTime.of(2023, 8, 13, 15, 30, 0);

        LocalDateTime result = DateProcessor.toDate(dateString);

        assertEquals(expectedDateTime, result);
    }

    @Test
    void toString_ValidLocalDateTime_ReturnsFormattedString() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 13, 15, 30, 0);
        String expectedDateString = "2023-08-13 15:30:00";

        String result = DateProcessor.toString(dateTime);

        assertEquals(expectedDateString, result);
    }
}
