package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Item item;
    private CommentRequest commentRequest;
    private Booking booking;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        user = new User(1, "username", "email");
        item = Item.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .owner(user)
                .available(true)
                .build();
        commentRequest = CommentRequest.builder()
                .text("comment text")
                .userId(1)
                .itemId(1)
                .build();
        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusMinutes(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        comment1 = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        comment2 = Comment.builder()
                .id(2)
                .text("text2")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void saveComment_ValidData_ReturnsCommentResponse() {
        item.setBookings(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findItemByIdWithBookingsFetched(anyInt())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponse result = commentService.saveComment(commentRequest);

        assertNotNull(result);
        assertEquals(commentRequest.getText(), result.getText());
        assertNotNull(result.getCreated());
    }

    @Test
    void saveComment_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.saveComment(commentRequest));
    }

    @Test
    void saveComment_ItemNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRepository.findItemByIdWithBookingsFetched(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.saveComment(commentRequest));
    }

    @Test
    void getCommentsOfItem_ItemWithComments_ReturnsListOfCommentResponses() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        when(commentRepository.findAllByItem_Id(anyInt())).thenReturn(comments);

        List<CommentResponse> result = commentService.getCommentsOfItem(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comment1.getText(), result.get(0).getText());
        assertEquals(comment2.getText(), result.get(1).getText());
    }

    @Test
    void getCommentsOfItem_ItemWithoutComments_ReturnsEmptyList() {
        when(commentRepository.findAllByItem_Id(1)).thenReturn(new ArrayList<>());

        List<CommentResponse> result = commentService.getCommentsOfItem(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemIdToComments_ReturnsMappedComments() {
        when(commentRepository.findAllByItems(anySet()))
                .thenReturn(Arrays.asList(comment1, comment2));

        Set<Integer> itemIds = new HashSet<>(Arrays.asList(1, 2));

        Map<Integer, List<CommentResponse>> result = commentService.getItemIdToComments(itemIds);

        assertNotNull(result);
        assertEquals(1, result.size());

        List<CommentResponse> item1Comments = result.get(1);
        assertNotNull(item1Comments);
        assertEquals(2, item1Comments.size());

        List<CommentResponse> item2Comments = result.get(1);
        assertNotNull(item2Comments);
        assertEquals(2, item2Comments.size());
    }

    @Test
    void getItemIdToComments_EmptyRepository_ReturnsEmptyMap() {
        when(commentRepository.findAllByItems(anySet()))
                .thenReturn(Collections.emptyList());

        Set<Integer> itemIds = new HashSet<>(Arrays.asList(1, 2));

        Map<Integer, List<CommentResponse>> result = commentService.getItemIdToComments(itemIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
