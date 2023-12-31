package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerResponse booker;
    private ItemBookingResponse item;
}
