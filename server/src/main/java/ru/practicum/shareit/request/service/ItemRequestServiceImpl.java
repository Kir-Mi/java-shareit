package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestResponse addItemRequest(ItemRequestDto dto) {
        User requestor = userRepository.findById(dto.getRequestorId())
                .orElseThrow(() -> {
                    return new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
                });
        ItemRequest request = ItemRequestMapper.mapToDomain(dto, requestor);
        request = itemRequestRepository.save(request);
        List<Item> items = itemRepository.findAllByRequest_Id(request.getId());
        return ItemRequestMapper.mapToDto(request, items);
    }

    @Override
    public List<ItemRequestResponse> getItemRequestsOfUser(Integer userId) {
        checkUserExists(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId);

        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findAllByRequest_Id(itemRequest.getId());
                    return ItemRequestMapper.mapToDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getItemRequestsNotOfUser(Integer userId, int from, int size) {
        Pageable pageable = calculatePageable(from, size, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllNotOfUser(userId, pageable);

        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findAllByRequest_Id(itemRequest.getId());
                    return ItemRequestMapper.mapToDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse getItemRequestById(Integer id, Integer userid) {
        checkUserExists(userid);
        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = String.format("Запрос с ID=%d не найден", id);
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
        List<Item> items = itemRepository.findAllByRequest_Id(request.getId());
        return ItemRequestMapper.mapToDto(request, items);
    }

    private void checkUserExists(Integer userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            String msg = String.format("Пользователь с ID=%d не найден", userId);
            throw new NotFoundException(msg, HttpStatus.NOT_FOUND);
        }
    }

    private Pageable calculatePageable(int id, int itemCount, String sortBy) {
        int itemsPerPage = itemCount;
        int pageNumber = (int) Math.floor((double) id / itemsPerPage);
        Sort sort = Sort.by(Sort.Order.desc(sortBy));
        return PageRequest.of(pageNumber, itemsPerPage, sort);
    }
}