package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(int userId, ItemDto itemDto);

    ItemDto update(int userId, int itemId, ItemDto itemDto);

    ItemDto getItemById(int itemId);

    List<ItemDto> getItemsByUser(int userId);

    List<ItemDto> search(String text);
}
