package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class User {
    private int id;
    private String name;
    @Email(message = "Некорректный email")
    @NotEmpty(message = "email не может быть пустым")
    @NonNull
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
