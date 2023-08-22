package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingDataValidator bookingDataValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user1;
    private User user2;
    private BookingRequest bookingRequest;
    private Item item;


    @BeforeEach
    void setUp() {
        user1 = new User(1, "name", "email");
        user2 = new User(2, "name2", "email2");
        bookingRequest = BookingRequest.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .itemId(1)
                .bookerId(1)
                .status(WAITING)
                .build();
        item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .build();
    }

    @Test
    void createBooking_ValidData_ReturnsBookingResponse() {
        User booker = user1;
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);

        when(userRepository.findById(bookingRequest.getBookerId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse result = bookingService.createBooking(bookingRequest);

        assertNotNull(result);
        assertEquals(booking, BookingMapper.mapToDomain(bookingRequest, booker, item));
    }

    @Test
    void createBooking_InvalidData_ThrowsValidationException() {
        bookingRequest.setStart(bookingRequest.getStart().plusHours(2));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_InvalidBooker_ThrowsNotFoundException() {

        when(userRepository.findById(bookingRequest.getBookerId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_InvalidItem_ThrowsNotFoundException() {
        User booker = user1;

        when(userRepository.findById(bookingRequest.getBookerId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_ItemNotAvailable_ThrowsValidationException() {
        User booker = user1;
        item.setAvailable(false);

        when(userRepository.findById(bookingRequest.getBookerId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.of(item));

        doThrow(new ValidationException("Item not available", HttpStatus.BAD_REQUEST))
                .when(bookingDataValidator).throwIfItemNotAvailable(anyInt(), any(Booking.class));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequest));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_BookerIsItemOwner_ThrowsValidationException() {
        User booker = user1;

        when(userRepository.findById(bookingRequest.getBookerId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingRequest.getItemId())).thenReturn(Optional.of(item));

        doThrow(new NotFoundException("Booker is item owner", HttpStatus.NOT_FOUND))
                .when(bookingDataValidator).throwIfBookerIsItemOwner(anyInt(), any(Booking.class));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequest));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateApproved_BookingApproved_ThrowsValidationException() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);
        booking.setStatus(APPROVED);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));
        doNothing().when(bookingDataValidator).throwIfNotOwnerOfBooking(anyInt(), anyInt(), any(Booking.class));
        doThrow(new ValidationException("Booking already approved", HttpStatus.BAD_REQUEST))
                .when(bookingDataValidator).throwIfBookingAlreadyApproved(anyInt(), any(Booking.class));

        assertThrows(ValidationException.class, () -> bookingService.updateApproved(1, 1, true));
    }

    @Test
    void updateApproved_BookingRejected_ThrowsValidationException() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);
        booking.setStatus(REJECTED);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));
        doNothing().when(bookingDataValidator).throwIfNotOwnerOfBooking(anyInt(), anyInt(), any(Booking.class));
        doThrow(new ValidationException("Booking already rejected", HttpStatus.BAD_REQUEST))
                .when(bookingDataValidator).throwIfBookingAlreadyRejected(anyInt(), any(Booking.class));

        assertThrows(ValidationException.class, () -> bookingService.updateApproved(1, 1, false));
    }

    @Test
    void updateApproved_BookingNotApproved_StatusSetToApproved() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);
        booking.setStatus(WAITING);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.updateApproved(1, 1, true);

        assertEquals(APPROVED, response.getStatus());
    }

    @Test
    void updateApproved_BookingNotRejected_StatusSetToRejected() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);
        booking.setStatus(WAITING);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.updateApproved(1, 1, false);

        assertEquals(REJECTED, response.getStatus());
    }

    @Test
    void getBookingById_UserIsBooker_Success() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);
        booking.setBooker(user1);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(1, 1);

        assertNotNull(response);
    }

    @Test
    void getBookingById_UserIsOwner_Success() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user1, item);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(1, 1);

        assertNotNull(response);
    }

    @Test
    void getBookingById_UserHasNoAccess_ThrowsNotFoundException() {
        Booking booking = BookingMapper.mapToDomain(bookingRequest, user2, item);
        item.setOwner(user1);
        booking.setItem(item);

        when(bookingRepository.findBookingByIdItemFetched(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1, 3));
    }

    @Test
    void getAllBookingsOfUser_AllState_ReturnsListOfBookings() {
        User booker = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, booker, item));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(eq(booker.getId()), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.ALL, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfUser_UnknownState_ThrowsValidationException() {
        User booker = user1;

        doNothing().when(bookingDataValidator).throwIfUserNotExists(anyInt());

        assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.UNKNOWN, 0, 10));
    }

    @Test
    void getAllBookingsOfUser_UserNotExists_ThrowsNotFoundException() {
        doThrow(new NotFoundException("User not found", HttpStatus.NOT_FOUND))
                .when(bookingDataValidator).throwIfUserNotExists(anyInt());
        assertThrows(NotFoundException.class,

                () -> bookingService.getAllBookingsOfUser(1, BookingStateDto.ALL, 0, 10));
    }

    @Test
    void getAllBookingsOfUser_AllCurrent_ReturnsListOfBookings() {
        User booker = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, booker, item));

        LocalDateTime now = LocalDateTime.now();
        doReturn(bookings).when(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(booker.getId()), any(), any(), any(Pageable.class));

        List<BookingResponse> result = bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.CURRENT, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfUser_PastState_ReturnsListOfBookings() {
        User booker = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, booker, item));

        LocalDateTime now = LocalDateTime.now();
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.PAST, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfUser_FutureState_ReturnsListOfBookings() {
        User booker = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, booker, item));

        LocalDateTime now = LocalDateTime.now();
        doReturn(bookings).when(bookingRepository).findAllByBookerIdAndStartAfterOrderByStartDesc(
                eq(booker.getId()), any(), any(Pageable.class));

        List<BookingResponse> result = bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.FUTURE, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfUser_WaitingState_ReturnsListOfBookings() {
        User booker = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, booker, item));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(WAITING), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.WAITING, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfUser_RejectedState_ReturnsListOfBookings() {
        User booker = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, booker, item));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(REJECTED), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfUser(booker.getId(), BookingStateDto.REJECTED, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfOwner_AllState_ReturnsListOfBookings() {
        User owner = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, user1, item));

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(owner.getId()), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.ALL, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfOwner_UnknownState_ThrowsValidationException() {
        User owner = user1;

        assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.UNKNOWN, 0, 10));
    }

    @Test
    void getAllBookingsOfOwner_UserNotExists_ThrowsNotFoundException() {
        doThrow(new NotFoundException("User not found", HttpStatus.NOT_FOUND))
                .when(bookingDataValidator).throwIfUserNotExists(anyInt());
        assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsOfOwner(1, BookingStateDto.ALL, 0, 10));
    }

    @Test
    void getAllBookingsOfOwner_AllCurrent_ReturnsListOfBookings() {
        User owner = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, user1, item));

        LocalDateTime now = LocalDateTime.now();
        doReturn(bookings).when(bookingRepository).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(owner.getId()), any(), any(), any(Pageable.class));

        List<BookingResponse> result = bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.CURRENT, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfOwner_PastState_ReturnsListOfBookings() {
        User owner = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, user1, item));

        LocalDateTime now = LocalDateTime.now();
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(owner.getId()), any(), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.PAST, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfOwner_FutureState_ReturnsListOfBookings() {
        User owner = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, user1, item));

        LocalDateTime now = LocalDateTime.now();
        doReturn(bookings).when(bookingRepository).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                eq(owner.getId()), any(), any(Pageable.class));

        List<BookingResponse> result = bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.FUTURE, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfOwner_WaitingState_ReturnsListOfBookings() {
        User owner = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, user1, item));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), eq(WAITING), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.WAITING, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    void getAllBookingsOfOwner_RejectedState_ReturnsListOfBookings() {
        User owner = user1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(BookingMapper.mapToDomain(bookingRequest, user1, item));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), eq(REJECTED), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingResponse> result = bookingService.getAllBookingsOfOwner(owner.getId(), BookingStateDto.REJECTED, 0, 10);

        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }
}