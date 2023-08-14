package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto create(@RequestHeader(name = USER_ID_HEADER, required = true) int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(name = USER_ID_HEADER, required = true) int userId,
                          @PathVariable("itemId") int itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(name = USER_ID_HEADER, required = true) int userId,
                               @PathVariable("itemId") int itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(USER_ID_HEADER) int userId,
                                        @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) @PositiveOrZero int from,
                                        @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) @PositiveOrZero int size) {
        return itemService.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) @PositiveOrZero int from,
                                @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) @PositiveOrZero int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse postComment(@PathVariable("itemId") Integer itemId,
                                       @RequestHeader(USER_ID_HEADER) Integer userId,
                                       @RequestBody @Valid CommentRequest dto) {
        dto.setUserId(userId);
        dto.setItemId(itemId);
        return commentService.saveComment(dto);
    }
}
