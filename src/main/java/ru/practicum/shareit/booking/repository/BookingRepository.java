package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("select b from Booking b join fetch b.booker bkr join fetch b.item i where b.id =:bookingId")
    Optional<Booking> findBookingByIdItemFetched(@Param("bookingId") Integer bookingId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);
}
