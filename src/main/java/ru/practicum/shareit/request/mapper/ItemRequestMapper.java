package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestResponse mapToDto(ItemRequest domain, List<Item> items) {
        List<ItemResponse> itemResponses = items
                .stream()
                .map(i -> mapItem(i, domain.getId()))
                .collect(Collectors.toList());
        return ItemRequestResponse.builder()
                .id(domain.getId())
                .description(domain.getDescription())
                .created(domain.getCreated())
                .items(itemResponses)
                .build();
    }

    public ItemRequest mapToDomain(ItemRequestDto dto, User requestor) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .build();
    }

    private ItemResponse mapItem(Item item, int requestId) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .requestId(requestId)
                .build();
    }
}