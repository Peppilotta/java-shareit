package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithProposalsDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String REQUEST_DATE_FIELD_NAME = "created";
    private final RequestService requestService;

    @PostMapping()
    public RequestDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @Valid @RequestBody RequestDto requestDto) {
        return requestService.save(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public RequestWithProposalsDto getRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                              @PathVariable Long requestId) {
        return requestService.getRequest(userId, requestId);
    }

    @GetMapping("")
    public List<RequestWithProposalsDto> getRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestWithProposalsDto> getRequestsPageable(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, REQUEST_DATE_FIELD_NAME));
        return requestService.getPartOfRequests(userId, pageable);
    }
}
