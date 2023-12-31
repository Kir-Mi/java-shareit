package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = USER_HEADER) Integer userId,
                                             @RequestBody @Valid ItemDto dto) {
        log.info(
                "Received POST request to create Item {} by user with id = {}",
                dto,
                userId
        );
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = USER_HEADER) Integer userId,
                                             @RequestBody ItemDto dto,
                                             @PathVariable("itemId") Integer itemId) {
        log.info(
                "Received PATCH request to update Item {} by user with id = {}",
                dto,
                userId
        );
        return itemClient.updateItem(userId, dto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") Integer itemId,
                                              @RequestHeader(USER_HEADER) Integer userId) {
        log.info("Received request to GET Item by id = {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsForUser(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
                                                  @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        log.info("Received request to GET items for user with id={}", userId);
        return itemClient.getItemsForUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String query,
                                              @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
                                              @RequestParam(value = "size", defaultValue = "10", required = false) @PositiveOrZero int size) {
        log.info("Received GET request to search for items by query = {}", query);
        return itemClient.searchItems(query, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable("itemId") Integer itemId,
                                              @RequestHeader(USER_HEADER) Integer userId,
                                              @RequestBody @Valid CommentDto dto) {
        log.info(
                "Received POST request to create comment to item with ID={} by user with ID={}",
                itemId,
                userId
        );
        return itemClient.postComment(itemId, userId, dto);
    }

}
