package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GatewayRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody RequestInputDto requestDto) {
        log.info("Add request for item with description = {} from user with id = {}", requestDto.toString(), userId);
        return requestClient.save(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable @Positive Long requestId) {
        log.info("Get request with id = {} from user with id = {}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping("")
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get all requests from user with id = {}", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsPageable(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Get all requests from user with id = {} Pageable from={} size={}", userId, from, size);
        return requestClient.getPartOfRequests(userId, from, size);
    }
}