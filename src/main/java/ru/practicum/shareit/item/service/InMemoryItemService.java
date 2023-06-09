package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class InMemoryItemService implements ItemService {
    private final ItemStorage itemStorage;

    private final ItemMapper itemMapper;

    private final UserStorage userStorage;

    public InMemoryItemService(ItemStorage itemStorage, ItemMapper itemMapper, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.info("Create request for itemDto {}", itemDto);
        checkUserExists(userId);
        checkItemIsAvailable(itemDto);
        Item item = itemStorage.createItem(itemMapper.mapFromItemDto(userId, itemDto));
        return itemMapper.mapFromItem(item);
    }

    @Override
    public ItemDto updateItem(long userId, long id, Map<String, Object> updates) {
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
        return itemMapper.mapFromItem(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        log.info("Get request for item with id={}", id);
        checkUserExists(userId);
        checkItemExists(id);
        return itemMapper.mapFromItem(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        log.info("Get request for items of user with id={}", userId);
        checkUserExists(userId);
        return itemStorage.getItems(userId).stream()
                .map(itemMapper::mapFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto deleteItem(long userId, long id) {
        log.info("Delete request for item with id={}", id);
        ItemDto itemDto = getItem(userId, id);
        checkItemOwnerId(userId, id);
        itemStorage.deleteItem(id);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItem(long userId, String keyWord) {
        log.info("Get request for item owned by user with id={}", userId);
        checkUserExists(userId);
        return new ArrayList<>(itemStorage.searchItem(keyWord).stream()
                .map(itemMapper::mapFromItem)
                .collect(Collectors.toList()));
    }

    private void checkItemExists(long id) {
        if (!itemStorage.checkItemExistence(id)) {
            throw new ItemDoesNotExistException("Item with id=" + id + " not exists.");
        }
    }

    private void checkUserExists(long id) {
        if (!userStorage.checkUserExistence(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void checkItemOwnerId(long userId, long id) {
        if (!Objects.equals(userId, itemStorage.getItem(id).getUserId())) {
            throw new NotOwnerException("User with id=" + userId + "  is not owner of item with id=" + id);
        }
    }

    private void checkItemIsAvailable(ItemDto item) {
        if (!item.isAvailable()) {
            throw new BadRequestException("Item field AVAILABLE is absent");
        }
    }
}