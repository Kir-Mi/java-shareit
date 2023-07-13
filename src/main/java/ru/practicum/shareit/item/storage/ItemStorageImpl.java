package ru.practicum.shareit.item.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage {

    private Map<Integer, Map<Integer, Item>> items = new HashMap<>();
    private int idCounter = 1;

    @Override
    public Item create(int userId, Item item) {
        item.setId(getNewId());
        item.setOwner(userId);
        Map<Integer, Item> newItem = items.get(userId);
        newItem.put(item.getId(), item);
        return newItem.get(item.getId());
    }

    @Override
    public void update(int userId, Item item) {
        Map<Integer, Item> itemMapByUser = items.get(userId);
        if (itemMapByUser == null) {
            throw new NotFoundException("Не найден пользователь или товар", HttpStatus.NOT_FOUND);
        }
        if (itemMapByUser.get(item.getId()) == null) {
            throw new NotFoundException("Не найден товар", HttpStatus.NOT_FOUND);
        }
        items.get(userId).put(item.getId(), item);
    }

    @Override
    public Item getItemById(int itemId) {
        for (Map<Integer, Item> itemsByUser : items.values()) {
            for (Item item : itemsByUser.values()) {
                if (item.getId() == itemId) {
                    return item;
                }
            }
        }
        throw new NotFoundException("Товар не найден", HttpStatus.NOT_FOUND);
    }

    @Override
    public List<Item> getItemsByUser(int userId) {
        return new ArrayList<>(items.get(userId).values());
    }

    @Override
    public List<Item> search(String text) {
        List<Item> matchingItems = new ArrayList<>();
        String regex = ".*" + text.toLowerCase() + ".*";

        for (Map<Integer, Item> itemsByUser : items.values()) {
            for (Item item : itemsByUser.values()) {
                String itemName = item.getName().toLowerCase();
                String itemDescription = item.getDescription().toLowerCase();

                if (itemName.matches(regex) || itemDescription.matches(regex)) {
                    if (item.getAvailable()) {
                        matchingItems.add(item);
                    }
                }
            }
        }
        return matchingItems;
    }

    private int getNewId() {
        return idCounter++;
    }

    @Override
    public boolean isUserExist(int userId) {
        if (items.containsKey(userId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addNewUserToMap(int userId) {
        items.put(userId, new HashMap<Integer, Item>());
    }

    @Override
    public void deleteUserFromMap(int userId) {
        items.remove(userId);
    }
}
