package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getUserById(int id);

    UserDto create(UserDto userDto);

    UserDto update(int id, UserDto userDto);

    void delete(int id);
}
