package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public Item mapFromItemDto(long userId, ItemDto itemDto) {
        return new Item().toBuilder()
                .id(itemDto.getId())
                .userId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.isAvailable())
                .build();
    }

    public ItemDto mapFromItem(Item item) {
        return new ItemDto().toBuilder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }
}