package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.ItemBookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@UtilityClass
public class BookingMapper {

    public BookingResponse mapToDto(Booking domain) {
        return BookingResponse.builder()
                .id(domain.getId())
                .start(domain.getStart())
                .end(domain.getEnd())
                .status(domain.getStatus())
                .booker(BookerResponse.builder()
                        .id(domain.getBooker().getId())
                        .build())
                .item(ItemBookingResponse.builder()
                        .id(domain.getItem().getId())
                        .name(domain.getItem().getName())
                        .build())
                .build();
    }

    public Booking mapToDomain(BookingRequest dto, UserRepository userRepository, ItemRepository itemRepository) {
        User booker = userRepository.findById(dto.getBookerId())
                .orElseThrow(() -> {
                    String msg = String.format("User with ID=%d not found.", dto.getBookerId());
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> {
                    String msg = String.format("Item with ID=%d not found.", dto.getItemId());
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(dto.getStatus())
                .build();
    }
}