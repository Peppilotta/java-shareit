package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GatewayItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Request all items Pageable from={} size={}", from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable @Positive long itemId) {
        log.info("Request item with id = {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestBody @Valid ItemInputDto item) {
        log.info("Add new item {}", item.toString());
        return itemClient.createItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable @Positive long itemId) {
        log.info("Delete item with id = {}", itemId);
        return itemClient.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestBody Map<String, Object> userUpdates,
                                         @PathVariable long itemId) {
        log.info("Update item with id = {}", itemId);
        return itemClient.updateItem(userId, itemId, userUpdates);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam String text,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Request all items with text={} Pageable from={} size={}", text, from, size);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable @Positive Long itemId,
                                              @Valid @RequestBody CommentInputDto commentDto) {
        log.info("Add comment text={} to item with id={}", commentDto.toString(), itemId);
        return itemClient.saveComment(itemId, userId, commentDto);
    }
}