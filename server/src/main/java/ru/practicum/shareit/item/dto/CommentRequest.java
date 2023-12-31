package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class CommentRequest {
    @NotEmpty
    private String text;
    private Integer userId;
    private Integer itemId;
}
