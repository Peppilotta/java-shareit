package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.model.Item;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toDto(Item item);
}