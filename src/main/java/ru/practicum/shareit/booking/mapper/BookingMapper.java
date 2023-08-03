package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.ItemBookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

    public Booking mapToDomain(BookingRequest dto, User booker, Item item) {
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