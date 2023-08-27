package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItem_Id(Integer itemId);

    @Query("select c from Comment c join fetch c.item i where c.item.id in :itemIds")
    List<Comment> findAllByItems(@Param("itemIds") Set<Integer> itemIds);
}
