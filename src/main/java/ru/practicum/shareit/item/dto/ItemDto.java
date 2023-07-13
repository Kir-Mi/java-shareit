package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
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

    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
