package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(int userId, ItemDto itemDto);

    ItemDto update(int userId, int itemId, ItemDto itemDto);

    ItemDto getItemById(int userId, int itemId);

    List<ItemDto> getItemsByUser(int userId, int from, int size);

    List<ItemDto> search(String text, int from, int size);
}
