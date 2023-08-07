package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(USER_ID_HEADER) long userId,
                                  @RequestParam(required = false) Optional<Integer> from,
                                  @RequestParam(required = false) Optional<Integer> size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_ID_HEADER) Long userId,
                       @RequestBody @Valid ItemDto item) {
        return itemService.createItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long itemId) {
        return itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
                          @RequestBody Map<String, Object> userUpdates, @PathVariable long itemId) {
        return itemService.updateItem(userId, itemId, userUpdates);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID_HEADER) long userId,
                                @RequestParam(required = false) Optional<Integer> from,
                                @RequestParam(required = false) Optional<Integer> size,
                                @RequestParam String text) {
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment
            (@PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto,
             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.saveComment(itemId, userId, commentDto);
    }
}