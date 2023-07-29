package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .ownerId(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .id(item.getId())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(ItemDto itemDto, UserRepository userRepository) {
        User owner = userRepository.findById(itemDto.getOwnerId())
                .orElseThrow(() -> {
                    String msg = String.format("User with ID=%d not found.", itemDto.getOwnerId());
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .available(itemDto.getAvailable())
                .build();
    }
}
