package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validator.DateProcessor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentResponse {
    private Integer id;
    private String text;
    private String authorName;
    @JsonFormat(pattern = DateProcessor.DATE_FORMAT)
    private LocalDateTime created;
}
