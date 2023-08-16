package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingStateDto;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest dto);

    BookingResponse updateApproved(Integer userId, Integer bookingId, boolean approved);

    BookingResponse getBookingById(Integer bookingId, Integer userId);

    List<BookingResponse> getAllBookingsOfUser(Integer bookerId, BookingStateDto state, int from, int size);

    List<BookingResponse> getAllBookingsOfOwner(Integer ownerId, BookingStateDto state, int from, int size);
}
