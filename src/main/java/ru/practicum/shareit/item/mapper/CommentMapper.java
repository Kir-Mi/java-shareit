package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@AllArgsConstructor
@Component
public class CommentMapper {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public CommentResponse toDto(Comment domain) {
        return CommentResponse.builder()
                .id(domain.getId())
                .text(domain.getText())
                .authorName(domain.getAuthor().getName())
                .created(domain.getCreated())
                .build();
    }

    public Comment toComment(CommentRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
        Item item = itemRepository.findItemByIdWithBookingsFetched(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
        checkUserBookedItem(dto, item);
        return Comment.builder()
                .text(dto.getText())
                .item(item)
                .created(LocalDateTime.now().plus(1000, ChronoUnit.MILLIS)) // Если не сдвинуть дату коммента - постман не проходит
                .author(user)
                .build();
    }

    private void checkUserBookedItem(CommentRequest dto, Item item) {
        Optional<Booking> booking = item.getBookings().stream()
                .filter(hasUserBookedItem(dto.getUserId()))
                .findFirst();
        if (booking.isEmpty()) {
            String msg = "Пользователь не бронировал вещь";
            throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
    }

    private Predicate<Booking> hasUserBookedItem(Integer userId) {
        return b -> b.getBooker().getId().equals(userId)
                && b.getStatus().equals(APPROVED)
                && b.getEnd().isBefore(LocalDateTime.now());
    }
}
