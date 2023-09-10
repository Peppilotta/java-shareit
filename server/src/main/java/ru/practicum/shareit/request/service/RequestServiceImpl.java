package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.RequestWithProposalsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    private final UserService userService;

    private final ItemService itemService;

    @Override
    public RequestDto save(Long userId, RequestDto requestDto) {
        checkUserExists(userId);
        Request request = requestMapper.toRequest(requestDto);
        request.setRequester(userService.getUser(userId));
        request.setCreated(LocalDateTime.now());
        Request saveRequest = requestRepository.save(request);
        return requestMapper.toDto(saveRequest);
    }

    @Override
    public RequestWithProposalsDto getRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        checkRequestExists(requestId);
        Request request = requestRepository.findById(requestId).get();
        RequestWithProposalsDto requestWithProposalsDto = requestMapper.toRequestWithProposalDto(request);
        requestWithProposalsDto.setItems(itemService.getItemsDtoByRequestId(request.getId()));
        return requestWithProposalsDto;
    }

    @Override
    public List<RequestWithProposalsDto> getRequests(Long userId) {
        checkUserExists(userId);
        List<RequestWithProposalsDto> requests = requestRepository.getAllByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(requestMapper::toRequestWithProposalDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        requests.forEach(request -> request.setItems(itemService.getItemsDtoByRequestId(request.getId())));
        return requests.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public List<RequestWithProposalsDto> getPartOfRequests(Long userId, Pageable pageable) {
        checkUserExists(userId);

        List<RequestWithProposalsDto> requests = requestRepository
                .getAllCreatedByOtherOrderByCreatedDesc(userId, pageable)
                .getContent()
                .stream()
                .map(requestMapper::toRequestWithProposalDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        requests.forEach(rQ -> rQ.setItems(itemService.getItemsDtoByRequestId(rQ.getId())));

        return requests.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private void checkUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void checkRequestExists(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new ItemDoesNotExistException("request with id: " + id + " not found");
        }
    }
}