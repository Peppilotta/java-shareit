package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long id, Map<String, Object> updates);

    ItemDto getItem(Long userId, Long id);

    List<ItemDto> getItems(Long userId);

    ItemDto deleteItem(Long userId, Long id);

    List<ItemDto> searchItem(Long userId, String keyWord);

    Comment saveComment(Long itemId, Long userId, CommentDto commentDto);
}