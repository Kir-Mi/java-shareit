package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingDataValidatorImplTest {

    private UserRepository userRepository;
    private BookingDataValidatorImpl validator;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        validator = new BookingDataValidatorImpl(userRepository);
    }

    @Test
    void throwIfNotOwnerOfBooking_OwnerIsNotOwner_ThrowsNotFoundException() {
        User owner = new User(1, "owner", "owner@example.com");
        User booker = new User(2, "booker", "booker@example.com");
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setOwner(owner);

        assertThrows(NotFoundException.class, () ->
                validator.throwIfNotOwnerOfBooking(booker.getId(), 1, booking));
    }

    @Test
    void throwIfNotOwnerOfBooking_OwnerIsOwner_NoExceptionThrown() {
        User owner = new User(1, "owner", "owner@example.com");
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setOwner(owner);

        assertDoesNotThrow(() ->
                validator.throwIfNotOwnerOfBooking(owner.getId(), 1, booking));
    }

    @Test
    void throwIfItemNotAvailable_ItemNotAvailable_ThrowsValidationException() {
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setAvailable(false);

        assertThrows(ValidationException.class, () ->
                validator.throwIfItemNotAvailable(1, booking));
    }

    @Test
    void throwIfItemNotAvailable_ItemAvailable_NoExceptionThrown() {
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setAvailable(true);

        assertDoesNotThrow(() ->
                validator.throwIfItemNotAvailable(1, booking));
    }

    @Test
    void throwIfUserNotExists_UserNotExists_ThrowsNotFoundException() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                validator.throwIfUserNotExists(1));
    }

    @Test
    void throwIfUserNotExists_UserExists_NoExceptionThrown() {
        when(userRepository.existsById(anyInt())).thenReturn(true);

        assertDoesNotThrow(() ->
                validator.throwIfUserNotExists(1));
    }

    @Test
    void throwIfBookerIsItemOwner_BookerIsOwner_ThrowsNotFoundException() {
        User owner = new User(1, "owner", "owner@example.com");
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setOwner(owner);

        assertThrows(NotFoundException.class, () ->
                validator.throwIfBookerIsItemOwner(owner.getId(), booking));
    }

    @Test
    void throwIfBookerIsItemOwner_BookerIsNotOwner_NoExceptionThrown() {
        User owner = new User(1, "owner", "owner@example.com");
        User booker = new User(2, "booker", "booker@example.com");
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setOwner(owner);

        assertDoesNotThrow(() ->
                validator.throwIfBookerIsItemOwner(booker.getId(), booking));
    }

    @Test
    void throwIfBookingAlreadyRejected_BookingRejected_ThrowsValidationException() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.REJECTED);

        assertThrows(ValidationException.class, () ->
                validator.throwIfBookingAlreadyRejected(1, booking));
    }

    @Test
    void throwIfBookingAlreadyRejected_BookingNotRejected_NoExceptionThrown() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        assertDoesNotThrow(() ->
                validator.throwIfBookingAlreadyRejected(1, booking));
    }

    @Test
    void throwIfBookingAlreadyApproved_BookingApproved_ThrowsValidationException() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);

        assertThrows(ValidationException.class, () ->
                validator.throwIfBookingAlreadyApproved(1, booking));
    }

    @Test
    void throwIfBookingAlreadyApproved_BookingNotApproved_NoExceptionThrown() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        assertDoesNotThrow(() ->
                validator.throwIfBookingAlreadyApproved(1, booking));
    }
}
