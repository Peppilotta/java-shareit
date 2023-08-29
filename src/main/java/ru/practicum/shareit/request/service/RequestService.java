package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithProposalsDto;

import java.util.List;

public interface RequestService {

    RequestDto save(Long userId, RequestDto requestDto);

    RequestWithProposalsDto getRequest(Long userId, Long requestId);

    List<RequestWithProposalsDto> getRequests(Long userId);

    List<RequestWithProposalsDto> getPartOfRequests(Long userId, Pageable pageable);
}