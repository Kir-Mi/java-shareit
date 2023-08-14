package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemRequestMapperTest {

    private User user1;
    private User user2;
    private ItemDto itemDto;
    private Item item1;
    private Item item2;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;


    @BeforeEach
    void setUp() {
        user1 = new User(1, "name", "email");
        user2 = new User(2, "name2", "email2");
        itemDto = ItemDto.builder()
                .id(1)
                .ownerId(1)
                .name("name")
                .description("description")
                .available(true)
                .build();
        item1 = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .bookings(new ArrayList<>())
                .build();
        item2 = Item.builder()
                .id(2)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .bookings(new ArrayList<>())
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .requestorId(1)
                .description("description")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("description")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void mapToDto_ValidData_ReturnsItemRequestResponse() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        ItemRequest request = itemRequest;

        ItemRequestResponse response = ItemRequestMapper.mapToDto(request, items);

        assertNotNull(response);
        assertEquals(request.getId(), response.getId());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getCreated(), response.getCreated());

        List<ItemResponse> itemResponses = response.getItems();
        assertEquals(items.size(), itemResponses.size());

        ItemResponse itemResponse1 = itemResponses.get(0);
        assertEquals(item1.getId(), itemResponse1.getId());
        assertEquals(item1.getName(), itemResponse1.getName());
        assertEquals(item1.getAvailable(), itemResponse1.isAvailable());
        assertEquals(item1.getDescription(), itemResponse1.getDescription());
        assertEquals(request.getId(), itemResponse1.getRequestId());

        ItemResponse itemResponse2 = itemResponses.get(1);
        assertEquals(item2.getId(), itemResponse2.getId());
        assertEquals(item2.getName(), itemResponse2.getName());
        assertEquals(item2.getAvailable(), itemResponse2.isAvailable());
        assertEquals(item2.getDescription(), itemResponse2.getDescription());
        assertEquals(request.getId(), itemResponse2.getRequestId());
    }

    @Test
    void mapToDomain_ValidData_ReturnsItemRequest() {
        ItemRequestDto dto = itemRequestDto;
        dto.setDescription("Request Description");

        User requestor = new User();

        ItemRequest itemRequest = ItemRequestMapper.mapToDomain(dto, requestor);

        assertNotNull(itemRequest);
        assertEquals(dto.getDescription(), itemRequest.getDescription());
        assertEquals(requestor, itemRequest.getRequestor());
    }
}
