package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(int userId, Item item);

    void update(int userId, Item item);

    Item getItemById(int itemId);

    List<Item> getItemsByUser(int userId);

    List<Item> search(String text);

    void addNewUserToMap(int userId);

    void deleteUserFromMap(int userId);

    boolean isUserExist(int userId);
}
