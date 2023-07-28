package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final BookingDataValidator bookingDataValidator;

    @Override
    public BookingResponse createBooking(BookingRequest dto) {
        if (dto.getStart().isAfter(dto.getEnd()) ||
                dto.getStart().equals(dto.getEnd())) {
            throw new ValidationException("Некорретные данные бронирования", HttpStatus.BAD_REQUEST);
        }
        dto.setStatus(WAITING);
        Booking booking = mapper.mapToDomain(dto);
        bookingDataValidator.throwIfItemNotAvailable(dto.getItemId(), booking);
        bookingDataValidator.throwIfBookerIsItemOwner(dto.getBookerId(), booking);
        Booking saved = bookingRepository.save(booking);
        return mapper.mapToDto(saved);
    }

    @Override
    public BookingResponse updateApproved(Integer userId, Integer bookingId, boolean approved) {
        Booking booking = findBookingByIdOrThrow(bookingId);
        bookingDataValidator.throwIfNotOwnerOfBooking(userId, bookingId, booking);
        if (approved) {
            bookingDataValidator.throwIfBookingAlreadyApproved(userId, booking);
            booking.setStatus(APPROVED);
        } else {
            bookingDataValidator.throwIfBookingAlreadyRejected(userId, booking);
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);
        return mapper.mapToDto(booking);
    }

    @Override
    public BookingResponse getBookingById(Integer bookingId, Integer userId) {
        Booking booking = findBookingByIdOrThrow(bookingId);
        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner().getId();
        if (userId.equals(bookerId) || userId.equals(ownerId)) {
            return mapper.mapToDto(booking);
        } else {
            String msg = String.format("У пользователя id=%d нет прав доступа", userId);
            throw new NotFoundException(msg, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<BookingResponse> getAllBookingsOfUser(Integer bookerId, BookingStateDto state) {
        bookingDataValidator.throwIfUserNotExists(bookerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, REJECTED);
                break;
            default:
                String msg = String.format("Unknown state: %s", state);
                throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
        return convertResponse(bookings);
    }

    @Override
    public List<BookingResponse> getAllBookingsOfOwner(Integer ownerId, BookingStateDto state) {
        bookingDataValidator.throwIfUserNotExists(ownerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, REJECTED);
                break;
            default:
                String msg = String.format("Unknown state: %s", state);
                throw new ValidationException(msg, HttpStatus.BAD_REQUEST);
        }
        return convertResponse(bookings);
    }

    private List<BookingResponse> convertResponse(List<Booking> bookings) {
        return bookings.stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    private Booking findBookingByIdOrThrow(Integer bookingId) {
        return bookingRepository.findBookingByIdItemFetched(bookingId)
                .orElseThrow(() -> {
                    String msg = String.format("Бронирование ID=%d не найдено", bookingId);
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
    }
}
