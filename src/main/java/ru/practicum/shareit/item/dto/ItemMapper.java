package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ItemMapper {

    ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    Item toItem(ItemDto itemDto);

    ItemDto toDto(Item item);
}