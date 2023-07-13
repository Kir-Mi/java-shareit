package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Вещь должна быть доступна для аренды", HttpStatus.BAD_REQUEST);
        }
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        Item item = itemStorage.create(userId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        Item updatedItem = itemStorage.getItemById(itemId);
        if (updatedItem == null || updatedItem.getOwner() != userId) {
            throw new NotFoundException("Пользователь или товар не найден", HttpStatus.NOT_FOUND);
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId) {
        if (!itemStorage.isUserExist(userId)) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        List<Item> items = itemStorage.getItemsByUser(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null) {
            return Collections.emptyList();
        }
        List<Item> items = itemStorage.search(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
