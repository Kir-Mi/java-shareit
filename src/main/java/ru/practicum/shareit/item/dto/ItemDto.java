package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    @NotNull
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;
    @NotNull
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;

}
