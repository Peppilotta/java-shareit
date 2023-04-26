package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(long id, Map<String, Object> updates);

    Item getItem(long id);

    List<Item> getItems(long userId);

    void deleteItem(long id);

    List<Item> searchItem(String keyWord);

    boolean checkItemExistence(long id);
}