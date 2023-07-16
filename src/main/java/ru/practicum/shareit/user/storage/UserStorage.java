package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User getUserById(int id);

    User create(User user);

    void update(int id, User user);

    void delete(int id);

    boolean isUserExist(int id);
}
