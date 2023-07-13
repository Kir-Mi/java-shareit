package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private int id;
    private String name;
    @Email(message = "Некорректный email")
    @NotEmpty(message = "email не может быть пустым")
    @NonNull
    private String email;

    public UserDto(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}