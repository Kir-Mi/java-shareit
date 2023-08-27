package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto_ReturnsCorrectDto() {
        User owner = User.builder().id(1).build();
        ItemRequest itemRequest = ItemRequest.builder().id(2).build();
        Item item = Item.builder()
                .id(3)
                .owner(owner)
                .name("Sample Item")
                .description("Description")
                .available(true)
                .request(itemRequest)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(owner.getId(), dto.getOwnerId());
        assertTrue(dto.getAvailable());
        assertEquals(itemRequest.getId(), dto.getRequestId());
    }

    @Test
    void toItemDto_NullRequest_ReturnsCorrectDto() {
        User owner = User.builder().id(1).build();
        Item item = Item.builder()
                .id(3)
                .owner(owner)
                .name("Sample Item")
                .description("Description")
                .available(true)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNull(dto.getRequestId());
    }

    @Test
    void toItem_ReturnsCorrectDomain() {
        ItemDto dto = ItemDto.builder()
                .id(1)
                .name("Sample Item")
                .description("Description")
                .ownerId(2)
                .available(true)
                .requestId(3)
                .build();
        User owner = User.builder().id(2).build();
        ItemRequest itemRequest = ItemRequest.builder().id(3).build();

        Item domain = ItemMapper.toItem(dto, owner, itemRequest);

        assertEquals(dto.getId(), domain.getId());
        assertEquals(dto.getName(), domain.getName());
        assertEquals(dto.getDescription(), domain.getDescription());
        assertEquals(owner, domain.getOwner());
        assertTrue(domain.getAvailable());
        assertEquals(itemRequest, domain.getRequest());
    }
}
