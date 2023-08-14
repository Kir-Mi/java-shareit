package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponse {
    private Integer id;
    private String name;
    private String description;
    private boolean available;
    private Integer requestId;
}