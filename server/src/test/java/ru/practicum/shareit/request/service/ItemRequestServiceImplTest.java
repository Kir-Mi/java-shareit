package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = new User(1, "name", "email");
        itemRequestDto = ItemRequestDto.builder()
                .requestorId(1)
                .description("description")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .bookings(new ArrayList<>())
                .build();
        item2 = Item.builder()
                .id(2)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .bookings(new ArrayList<>())
                .build();
    }

    @Test
    void addItemRequest_ValidDto_ReturnsItemRequestResponse() {
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByRequest_Id(anyInt())).thenReturn(List.of(item));

        ItemRequestResponse result = itemRequestService.addItemRequest(itemRequestDto);

        assertNotNull(result);
    }

    @Test
    void getItemRequestsOfUser_ValidUserId_ReturnsListOfItemRequestResponses() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestor_Id(1)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAllByRequest_Id(1)).thenReturn(Collections.singletonList(item));

        List<ItemRequestResponse> result = itemRequestService.getItemRequestsOfUser(1);

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemRequestResponse response = result.get(0);
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertNotNull(response.getCreated());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void getItemRequestsOfUser_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestsOfUser(1));
    }

    @Test
    void getItemRequestsNotOfUser_ValidUserId_ReturnsListOfItemRequestResponses() {
        when(itemRequestRepository.findAllNotOfUser(eq(1), any(Pageable.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(1)).thenReturn(Collections.singletonList(item));

        List<ItemRequestResponse> result = itemRequestService.getItemRequestsNotOfUser(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemRequestResponse response = result.get(0);
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertNotNull(response.getCreated());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void getItemRequestById_ValidIds_ReturnsItemRequestResponse() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(1)).thenReturn(Collections.singletonList(item));

        ItemRequestResponse result = itemRequestService.getItemRequestById(1, 1);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getCreated());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getItemRequestById_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(1, 1));
    }

    @Test
    void getItemRequestById_RequestNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(1, 1));
    }
}
