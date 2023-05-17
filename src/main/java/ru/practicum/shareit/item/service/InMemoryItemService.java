package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemService implements ItemService {
    private final ItemStorage itemStorage;

    private final ItemMapper itemMapper;

    private final CommentMapper commentMapper;

    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Create request for itemDto {}", itemDto);
        checkUserExists(userId);
        checkItemIsAvailable(itemDto);
        Item item = itemStorage.createItem(itemMapper.toItem(itemDto));
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, Map<String, Object> updates) {
        log.info("Update request for item with id={}", id);
        checkUserExists(userId);
        checkItemExists(id);
        checkItemOwnerId(userId, id);
        Item item = itemStorage.getItem(id);
        if (updates.containsKey("name")) {
            item.setName(String.valueOf(updates.get("name")));
        }
        if (updates.containsKey("description")) {
            item.setDescription(String.valueOf(updates.get("description")));
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.parseBoolean(String.valueOf(updates.get("available"))));
        }
        return itemMapper.toDto(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long id) {
        log.info("Get request for item with id={}", id);
        checkUserExists(userId);
        checkItemExists(id);
        return itemMapper.toDto(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.info("Get request for items of user with id={}", userId);
        checkUserExists(userId);
        return itemStorage.getItems(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto deleteItem(Long userId, Long id) {
        log.info("Delete request for item with id={}", id);
        ItemDto itemDto = getItem(userId, id);
        checkItemOwnerId(userId, id);
        itemStorage.deleteItem(id);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String keyWord) {
        log.info("Get request for item owned by user with id={}", userId);
        checkUserExists(userId);
        return new ArrayList<>(itemStorage.searchItem(keyWord).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList()));
    }

    private void checkItemExists(Long id) {
        if (!itemStorage.checkItemExistence(id)) {
            throw new ItemDoesNotExistException("Item with id=" + id + " not exists.");
        }
    }

    private void checkUserExists(Long id) {
        if (!userStorage.checkUserExistence(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void checkItemOwnerId(Long userId, Long id) {
        if (!Objects.equals(userId, itemStorage.getItem(id).getOwner().getId())) {
            throw new NotOwnerException("User with id=" + userId + "  is not owner of item with id=" + id);
        }
    }

    private void checkItemIsAvailable(ItemDto item) {
        if (!item.isAvailable()) {
            throw new BadRequestException("Item field AVAILABLE is absent");
        }
    }

    @Override
    public Comment saveComment(Long itemId, Long userId, CommentDto commentDto) {
        return commentMapper.toComment(commentDto);
    }
}