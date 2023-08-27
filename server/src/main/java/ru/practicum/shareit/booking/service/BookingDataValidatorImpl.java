package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class BookingDataValidatorImpl implements BookingDataValidator {

    private final UserRepository userRepository;

    @Override
    public void throwIfNotOwnerOfBooking(Integer ownerId, Integer bookingId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            String msg = String.format("user ID=%d не владеет вещью", ownerId);
            throw new NotFoundException(msg, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void throwIfItemNotAvailable(Integer itemId, Booking booking) {
        if (!booking.getItem().getAvailable()) {
            String msg = String.format("Вещь с ID=%d не доступна", itemId);
            throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void throwIfUserNotExists(Integer bookerId) {
        if (!userRepository.existsById(bookerId)) {
            String msg = String.format("Пользователь ID=%d не найден", bookerId);
            throw new NotFoundException(msg, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void throwIfBookerIsItemOwner(Integer bookerId, Booking booking) {
        if (bookerId.equals(booking.getItem().getOwner().getId())) {
            String msg = String.format("Пользователь ID=%d уже владеет вещью", bookerId);
            throw new NotFoundException(msg, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void throwIfBookingAlreadyRejected(Integer ownerId, Booking booking) {
        if (booking.getStatus() == REJECTED) {
            String msg = "В бронировании было отказано ранее";
            throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void throwIfBookingAlreadyApproved(Integer ownerId, Booking booking) {
        if (booking.getStatus() == APPROVED) {
            String msg = "Бронирование подтверждено ранее";
            throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
