package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Item {
    @NonNull
    private int id;
    @NonNull
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;
    @NonNull
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    @NonNull
    private Boolean available;
    private int owner;

}
