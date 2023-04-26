package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    long count = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(++count);
        items.put(count, item);
        return item;
    }

    @Override
    public Item updateItem(long id, Map<String, Object> updates) {
        Item item = items.get(id);
        if (updates.containsKey("name")) {
            item.setName(String.valueOf(updates.get("name")));
        }
        if (updates.containsKey("description")) {
            item.setDescription(String.valueOf(updates.get("description")));
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.valueOf(String.valueOf(updates.get("available"))));
        }
        return item;
    }

    @Override
    public Item getItem(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItems(long userId) {
        return items.values().stream().filter(i -> Objects.equals(i.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long id) {
        items.remove(id);
    }

    @Override
    public List<Item> searchItem(String keyWord) {
        String text = keyWord.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text))
                .filter(i -> i.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkItemExistence(long id) {
        return items.containsKey(id);
    }
}
