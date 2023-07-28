package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

    @Query("select i from Item i join fetch i.owner o where i.id = :itemId")
    Optional<Item> findByIdOwnerFetched(@Param("itemId") Integer itemId);

    List<Item> findAllByOwnerIdOrderByIdAsc(Integer id);

    @Query("select distinct i from Item i left join fetch i.bookings b where i.id = :itemId")
    Optional<Item> findItemByIdWithBookingsFetched(@Param("itemId") Integer itemId);

    @Query("select distinct i from Item i join fetch i.owner u left join fetch i.bookings b where u.id = :ownerId")
    List<Item> findAllByOwnerIdFetchBookings(@Param("ownerId") Integer ownerId);

}
