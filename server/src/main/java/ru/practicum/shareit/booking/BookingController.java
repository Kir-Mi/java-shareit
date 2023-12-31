package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid BookingRequest dto,
                                         @RequestHeader(USER_ID_HEADER) Integer userId) {
        dto.setBookerId(userId);
        return bookingService.createBooking(dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateStatus(@PathVariable("bookingId") Integer bookingId,
                                        @RequestParam("approved") boolean approved,
                                        @RequestHeader(USER_ID_HEADER) Integer userId) {
        return bookingService.updateApproved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@PathVariable("bookingId") Integer bookingId,
                                          @RequestHeader(USER_ID_HEADER) Integer userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getBookingsOfUser(@RequestHeader(USER_ID_HEADER) Integer bookerId,
                                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                   @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) @PositiveOrZero int from,
                                                   @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) @PositiveOrZero int size) {
        return bookingService.getAllBookingsOfUser(bookerId, BookingStateDto.fromString(state), from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsOfOwner(@RequestHeader(USER_ID_HEADER) Integer ownerId,
                                                    @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                    @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) @PositiveOrZero int from,
                                                    @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) @PositiveOrZero int size) {
        return bookingService.getAllBookingsOfOwner(ownerId, BookingStateDto.fromString(state), from, size);
    }
}