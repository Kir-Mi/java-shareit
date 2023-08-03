package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemBookingResponse {
    private Integer id;
    private String name;
}
