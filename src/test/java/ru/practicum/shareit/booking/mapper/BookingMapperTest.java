package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingMapperTest {

    @Test
    void mapToDto_ReturnsCorrectDto() {
        Booking booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.of(2023, 8, 10, 10, 0))
                .end(LocalDateTime.of(2023, 8, 10, 12, 0))
                .status(BookingStatus.WAITING)
                .booker(User.builder().id(2).build())
                .item(Item.builder().id(3).name("Sample Item").build())
                .build();

        BookingResponse dto = BookingMapper.mapToDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getItem().getName(), dto.getItem().getName());
    }

    @Test
    void mapToDomain_ReturnsCorrectDomain() {
        BookingRequest dto = BookingRequest.builder()
                .id(1)
                .start(LocalDateTime.of(2023, 8, 10, 10, 0))
                .end(LocalDateTime.of(2023, 8, 10, 12, 0))
                .status(BookingStatus.WAITING)
                .build();
        User booker = User.builder().id(2).build();
        Item item = Item.builder().id(3).name("Sample Item").build();

        Booking domain = BookingMapper.mapToDomain(dto, booker, item);

        assertEquals(dto.getId(), domain.getId());
        assertEquals(dto.getStart(), domain.getStart());
        assertEquals(dto.getEnd(), domain.getEnd());
        assertEquals(dto.getStatus(), domain.getStatus());
        assertEquals(booker, domain.getBooker());
        assertEquals(item, domain.getItem());
    }
}
