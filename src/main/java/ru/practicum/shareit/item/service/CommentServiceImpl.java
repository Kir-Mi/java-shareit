package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper mapper;

    @Override
    public CommentResponse saveComment(CommentRequest commentRequest) {
        Comment saved = commentRepository.save(mapper.toComment(commentRequest));
        return mapper.toDto(saved);
    }

    @Override
    public List<CommentResponse> getCommentsOfItem(Integer itemId) {
        return commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<CommentResponse>> getItemIdToComments(Set<Integer> itemIds) {
        return commentRepository.findAllByItems(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId(),
                        Collectors.mapping(mapper::toDto, Collectors.toList())));
    }
}
