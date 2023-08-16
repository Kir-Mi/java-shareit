package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private int id;
    private int ownerId;
    @NotNull
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;
    @NotNull
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;
    private List<CommentResponse> comments;
    private Integer requestId;

}
