package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

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
