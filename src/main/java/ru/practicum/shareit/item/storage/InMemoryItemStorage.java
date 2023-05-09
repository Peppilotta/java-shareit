package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    long idSequence = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(++idSequence);
        items.put(idSequence, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        long id = item.getId();
        items.put(id, item);
        return items.get(id);
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
    public List<Item> searchItem(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }
        String text = query.toLowerCase();
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text) ||
                        i.getDescription().toLowerCase().contains(text)) && i.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkItemExistence(long id) {
        return items.containsKey(id);
    }
}
