package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String msg = String.format("User with ID=%d not found.", itemDto.getOwnerId());
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
        itemDto.setOwnerId(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        Item toUpdate = findItemByIdOrThrow(itemId);
        checkUserExists(userId);
        checkItemBelongsToUser(toUpdate, userId);
        updateItem(itemDto, toUpdate);
        itemRepository.save(toUpdate);
        return ItemMapper.toItemDto(toUpdate);
    }

    @Override
    public ItemDto getItemById(int userId, int itemId) {
        Item item = findItemBookingsFetchedOrThrow(itemId);
        ItemDto dto = ItemMapper.toItemDto(item);
        setCommentsToDtoFromDb(dto);
        setBookings(dto, item, userId);
        return dto;
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId) {
        Map<Integer, Item> idToItem = getItemsMapFetchedWithBookings(userId);
        Map<Integer, List<CommentResponse>> itemIdToComments =
                commentService.getItemIdToComments(idToItem.keySet());
        return createItemDtos(idToItem, itemIdToComments);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(text);
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItemBelongsToUser(Item item, Integer ownerId) {
        if (!item.getOwner().getId().equals(ownerId)) {
            String msg = "Вещь не принадлежит пользователю";
            throw new ValidationException(msg, HttpStatus.FORBIDDEN);
        }
    }

    private void checkUserExists(Integer ownerId) {
        if (userRepository.getReferenceById(ownerId).getId() == null) {
            String msg = String.format("Пользователь ID=%d не найден", ownerId);
            throw new NotFoundException(msg, HttpStatus.NOT_FOUND);
        }
    }

    private Item findItemByIdOrThrow(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    String msg = String.format("Вещь id=%d не найдена", itemId);
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
    }

    private void updateItem(ItemDto patchDto, Item toUpdate) {
        updateAvailable(patchDto, toUpdate);
        updateDescription(patchDto, toUpdate);
        updateName(patchDto, toUpdate);
    }

    private void updateName(ItemDto patchDto, Item toUpdate) {
        if (patchDto.getName() != null) {
            toUpdate.setName(patchDto.getName());
        }
    }

    private void updateDescription(ItemDto patchDto, Item toUpdate) {
        if (patchDto.getDescription() != null) {
            toUpdate.setDescription(patchDto.getDescription());
        }
    }

    private void updateAvailable(ItemDto patchDto, Item toUpdate) {
        if (patchDto.getAvailable() != null) {
            toUpdate.setAvailable(patchDto.getAvailable());
        }
    }

    private Item findItemBookingsFetchedOrThrow(Integer itemId) {
        return itemRepository.findItemByIdWithBookingsFetched(itemId)
                .orElseThrow(() -> {
                    String msg = String.format("Вещь id=%d не найдена", itemId);
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
    }

    private void setCommentsToDtoFromDb(ItemDto dto) {
        List<CommentResponse> comments = commentService.getCommentsOfItem(dto.getId());
        dto.setComments(comments);
    }

    private Map<Integer, Item> getItemsMapFetchedWithBookings(Integer userId) {
        return itemRepository.findAllByOwnerIdFetchBookings(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
    }

    private List<ItemDto> createItemDtos(Map<Integer, Item> idToItem,
                                         Map<Integer, List<CommentResponse>> itemIdToComments) {
        return idToItem.values().stream()
                .map(i -> {
                    ItemDto dto = ItemMapper.toItemDto(i);
                    setBookings(dto, i, i.getOwner().getId());
                    addCommentsToDtoFromMem(dto, itemIdToComments);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void addCommentsToDtoFromMem(ItemDto dto,
                                         Map<Integer, List<CommentResponse>> itemIdToComments) {
        if (itemIdToComments.containsKey(dto.getId())) {
            dto.setComments(itemIdToComments.get(dto.getId()));
        }
    }

    private void setBookings(ItemDto dto, Item item, Integer userId) {
        if (userId.equals(dto.getOwnerId())) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = item.getBookings();
            bookings.stream()
                    .filter(isActiveAfterNow(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .ifPresent(addNextBooking(dto));

            bookings.stream()
                    .filter(isStartedAndApprovedBeforeNow(now))
                    .max(Comparator.comparing(Booking::getStart))
                    .ifPresent(addLastBookings(dto));
        }
    }

    private Consumer<Booking> addLastBookings(ItemDto dto) {
        return b -> {
            dto.setLastBooking(BookingResponseDto.builder()
                    .id(b.getId())
                    .bookerId(b.getBooker().getId())
                    .build());
        };
    }

    private Predicate<Booking> isStartedAndApprovedBeforeNow(LocalDateTime now) {
        return b -> (b.getEnd().isBefore(now) || (b.getEnd().isAfter(now) && b.getStart().isBefore(now)))
                && b.getStatus().equals(BookingStatus.APPROVED);
    }

    private Consumer<Booking> addNextBooking(ItemDto dto) {
        return b -> {
            dto.setNextBooking(BookingResponseDto.builder()
                    .id(b.getId())
                    .bookerId(b.getBooker().getId())
                    .build());
        };
    }

    private Predicate<Booking> isActiveAfterNow(LocalDateTime now) {
        return b -> b.getStart().isAfter(now)
                && (b.getStatus().equals(BookingStatus.WAITING)
                || b.getStatus().equals(BookingStatus.APPROVED));
    }

}
