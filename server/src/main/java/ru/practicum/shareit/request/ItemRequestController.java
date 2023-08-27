package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponse addRequest(@RequestHeader(value = USER_HEADER) @NotNull Integer userId,
                                          @RequestBody @Valid ItemRequestDto dto) {
        dto.setRequestorId(userId);
        return itemRequestService.addItemRequest(dto);
    }

    @GetMapping
    public List<ItemRequestResponse> getRequestsOfUser(@RequestHeader(value = USER_HEADER) @NotNull Integer userId) {
        return itemRequestService.getItemRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequests(@RequestHeader(value = USER_HEADER) @NotNull Integer userId,
                                                    @RequestParam(value = "from", defaultValue = "0", required = false) @Min(0) @PositiveOrZero int from,
                                                    @RequestParam(value = "size", defaultValue = "10", required = false) @Min(1) @Max(100) @PositiveOrZero int size) {
        return itemRequestService.getItemRequestsNotOfUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequest(@RequestHeader(value = USER_HEADER) @NotNull Integer userId,
                                          @PathVariable("requestId") Integer id) {
        return itemRequestService.getItemRequestById(id, userId);
    }
}
