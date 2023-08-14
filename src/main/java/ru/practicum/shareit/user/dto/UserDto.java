package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private int id;
    private String name;
    @Email(message = "Некорректный email")
    @NotEmpty(message = "email не может быть пустым")
    @NotNull
    private String email;

}
