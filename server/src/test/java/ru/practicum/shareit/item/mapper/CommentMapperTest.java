package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

class CommentMapperTest {

    @Test
    void toDto_ReturnsCorrectDto() {
        User author = User.builder().name("Alice").build();
        LocalDateTime now = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1)
                .text("Test comment")
                .author(author)
                .created(now)
                .build();

        CommentResponse dto = CommentMapper.toDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void toComment_ValidCommentRequest_ReturnsCorrectComment() {
        CommentRequest dto = CommentRequest.builder()
                .text("Test comment")
                .userId(1)
                .build();
        User user = User.builder().id(1).build();
        Item item = Item.builder()
                .id(2)
                .bookings(Collections.singletonList(
                        Booking.builder()
                                .booker(user)
                                .status(APPROVED)
                                .end(LocalDateTime.now().minusMinutes(30))
                                .build()))
                .build();

        Comment comment = CommentMapper.toComment(dto, user, item);

        assertEquals(dto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
    }

    @Test
    void toComment_InvalidUserCommentRequest_ThrowsValidationException() {
        CommentRequest dto = CommentRequest.builder()
                .text("Test comment")
                .userId(1)
                .build();
        User user = User.builder().id(1).build();
        Item item = Item.builder()
                .id(2)
                .bookings(Collections.singletonList(
                        Booking.builder()
                                .booker(user)
                                .status(APPROVED)
                                .end(LocalDateTime.now().plusMinutes(30))
                                .build()))
                .build();

        assertThrows(ValidationException.class, () -> CommentMapper.toComment(dto, user, item));
    }
}
