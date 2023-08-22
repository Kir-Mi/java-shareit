package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user1;
    private User user2;
    private ItemDto itemDto;
    private Item item;
    private Item item2;


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
        item = Item.builder()
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
    }

    @Test
    void create_ValidData_ReturnsCreatedItemDto() {
        User owner = user1;

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.create(1, itemDto);

        assertNotNull(result);
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(1, result.getOwnerId());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_OwnerNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(1, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void create_WithItemRequest_ValidData_ReturnsCreatedItemDto() {
        User owner = user1;

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.create(1, itemDto);

        assertNotNull(result);
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(1, result.getOwnerId());
    }

    @Test
    void create_WithItemRequest_ItemRequestNotFound_ThrowsNotFoundException() {
        itemDto.setRequestId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(1, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_ValidData_ReturnsUpdatedItemDto() {
        itemDto.setName("newName");
        itemDto.setDescription("newDescription");
        when(userRepository.getReferenceById(1)).thenReturn(user1);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.update(1, 1, itemDto);

        assertNotNull(result);
        assertEquals("newName", result.getName());
        assertEquals("newDescription", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void update_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(1, 1, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_UserNotFound_ThrowsNotFoundException() {
        user2.setId(null);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.getReferenceById(1)).thenReturn(user2);

        assertThrows(NotFoundException.class, () -> itemService.update(1, 1, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_ItemBelongsToDifferentUser_ThrowsNotFoundException() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.getReferenceById(2)).thenReturn(user2);

        assertThrows(ValidationException.class, () -> itemService.update(2, 1, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemById_ValidData_ReturnsItemDto() {
        when(itemRepository.findItemByIdWithBookingsFetched(1)).thenReturn(Optional.of(item));
        when(commentService.getCommentsOfItem(1)).thenReturn(List.of());

        ItemDto result = itemService.getItemById(1, 1);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void getItemById_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepository.findItemByIdWithBookingsFetched(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1, 1));
    }

    @Test
    void getItemsByUser_ValidData_ReturnsListOfItemDtos() {
        LocalDateTime now = LocalDateTime.now();
        List<CommentResponse> comments = new ArrayList<>();
        comments.add(new CommentResponse(1, "text", "authorName", now));
        comments.add(new CommentResponse(2, "text2", "authorName2", now));

        when(itemRepository.findAllByOwnerIdFetchBookings(eq(user1.getId()), any(Pageable.class)))
                .thenReturn(List.of(item, item2));
        when(commentService.getItemIdToComments(anySet()))
                .thenReturn(Collections.singletonMap(item.getId(), comments));

        List<ItemDto> result = itemService.getItemsByUser(user1.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());

        ItemDto dto1 = result.get(0);
        assertEquals(item.getId(), dto1.getId());
        assertEquals(item.getName(), dto1.getName());
        assertEquals(item.getDescription(), dto1.getDescription());
        assertTrue(dto1.getAvailable());
        assertEquals(comments.size(), dto1.getComments().size());

        ItemDto dto2 = result.get(1);
        assertEquals(item2.getId(), dto2.getId());
        assertEquals(item2.getName(), dto2.getName());
        assertEquals(item2.getDescription(), dto2.getDescription());
        assertTrue(dto2.getAvailable());
        assertNull(dto2.getComments());
    }

    @Test
    void getItemsByUser_EmptyResult_ReturnsEmptyList() {
        when(itemRepository.findAllByOwnerIdFetchBookings(eq(user1.getId()), any(Pageable.class)))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.getItemsByUser(user1.getId(), 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_ValidText_ReturnsListOfAvailableItems() {
        String searchText = "keyword";
        when(itemRepository.search(eq(searchText), any(Pageable.class))).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.search(searchText, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        ItemDto dto = result.get(0);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void search_EmptyText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.search("", 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_NullText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.search(null, 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_WhitespaceText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.search("   ", 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_NoAvailableItems_ReturnsEmptyList() {
        String searchText = "keyword";
        item.setAvailable(false);
        when(itemRepository.search(eq(searchText), any(Pageable.class))).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.search(searchText, 0, 10);

        assertTrue(result.isEmpty());
    }
}
