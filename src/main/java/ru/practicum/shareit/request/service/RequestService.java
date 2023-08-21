package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithProposalsDto;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    RequestDto save(Long userId, RequestDto requestDto);

    RequestWithProposalsDto getRequest(Long userId, Long requestId);

    List<RequestWithProposalsDto> getRequests(Long userId);

    List<RequestWithProposalsDto> getPartOfRequests(Long userId, Optional<Integer> from, Optional<Integer> lines);
}