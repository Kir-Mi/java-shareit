package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponse addItemRequest(ItemRequestDto dto);

    List<ItemRequestResponse> getItemRequestsOfUser(Integer userId);

    List<ItemRequestResponse> getItemRequestsNotOfUser(Integer userId, int from, int size);

    ItemRequestResponse getItemRequestById(Integer id, Integer userId);
}
