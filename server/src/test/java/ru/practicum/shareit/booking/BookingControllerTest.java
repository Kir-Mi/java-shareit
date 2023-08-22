package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.dto.ItemBookingResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;

    @Test
    void createBooking_ReturnsCreatedBooking() throws Exception {
        BookingResponse bookingResponse = createSampleBookingResponse();
        when(bookingService.createBooking(any())).thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateStatus_ReturnsUpdatedBooking() throws Exception {
        BookingResponse bookingResponse = createSampleBookingResponse();
        when(bookingService.updateApproved(1, 1, true)).thenReturn(bookingResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getBookingsOfUser_ReturnsListOfBookings() throws Exception {
        List<BookingResponse> bookingResponses = Collections.singletonList(createSampleBookingResponse());
        when(bookingService.getAllBookingsOfUser(1, BookingStateDto.ALL, 0, 10)).thenReturn(bookingResponses);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponses.get(0).getId()));
    }

    @Test
    void getBookingById_ReturnsBooking() throws Exception {
        BookingResponse bookingResponse = createSampleBookingResponse();
        when(bookingService.getBookingById(1, 1)).thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()));
    }

    @Test
    void getBookingsOfOwner_ReturnsListOfBookings() throws Exception {
        List<BookingResponse> bookingResponses = Collections.singletonList(createSampleBookingResponse());
        when(bookingService.getAllBookingsOfOwner(1, BookingStateDto.ALL, 0, 10)).thenReturn(bookingResponses);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    private BookingResponse createSampleBookingResponse() {
        return BookingResponse.builder()
                .id(1)
                .start(LocalDateTime.of(2024, 8, 10, 10, 0))
                .end(LocalDateTime.of(2024, 8, 10, 9, 0))
                .status(BookingStatus.WAITING)
                .booker(new BookerResponse(1))
                .item(new ItemBookingResponse(1, "item"))
                .build();
    }
}