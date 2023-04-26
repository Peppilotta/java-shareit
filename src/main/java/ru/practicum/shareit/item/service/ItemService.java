package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long id, Map<String, Object> updates);

    ItemDto getItem(long id);

    List<ItemDto> getItems(long userId);

    ItemDto deleteItem(long userId, long id);

    List<ItemDto> searchItem(String keyWord);

    void checkItemExistence(long id);

    void checkUserExistence(long id);

    void checkItemOwner(long userId, long id);
}