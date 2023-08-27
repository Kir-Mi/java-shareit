package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingDataValidator {
    void throwIfItemNotAvailable(Integer itemId, Booking booking);

    void throwIfBookingAlreadyRejected(Integer ownerId, Booking booking);

    void throwIfUserNotExists(Integer userId);

    void throwIfNotOwnerOfBooking(Integer ownerId, Integer bookingId, Booking booking);

    void throwIfBookerIsItemOwner(Integer userId, Booking booking);

    void throwIfBookingAlreadyApproved(Integer ownerId, Booking booking);
}
