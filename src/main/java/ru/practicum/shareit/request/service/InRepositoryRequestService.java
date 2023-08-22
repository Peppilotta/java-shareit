package ru.practicum.shareit.request.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.RequestWithProposalsDto;
import ru.practicum.shareit.request.model.QRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InRepositoryRequestService implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    private final UserService userService;

    @PersistenceContext
    EntityManager em;

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
        QRequest qRequest = QRequest.request;
        userService.getUser(userId);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        RequestWithProposalsDto request =
                requestMapper.toRequestWithProposalDto(queryFactory.selectFrom(qRequest)
                        .where(qRequest.id.eq(requestId))
                        .fetchOne());
        request.setItems(getItemsDto(request.getId()));
        return request;
    }

    @Override
    public List<RequestWithProposalsDto> getRequests(Long userId) {
        checkUserExists(userId);
        QRequest qRequest = QRequest.request;
        User user = userService.getUser(userId);
        JPAQuery<Request> query = new JPAQuery<>(em);
        List<RequestWithProposalsDto> requests;
        requests = query.from(qRequest)
                .where(qRequest.requester.id.eq(user.getId()))
                .orderBy(qRequest.created.desc())
                .fetch()
                .stream()
                .map(requestMapper::toRequestWithProposalDto)
                .collect(Collectors.toList());
        requests.forEach(request -> request.setItems(getItemsDto(request.getId())));
        return requests.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public List<RequestWithProposalsDto> getPartOfRequests(Long userId,
                                                           Optional<Integer> from, Optional<Integer> size) {
        checkFromAndSize(from, size);
        checkUserExists(userId);
        QRequest request = QRequest.request;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        long totalItems = requestRepository.count() + 1;
        int fromExist = 0;
        if (from.isPresent()) {
            fromExist = from.get();
            if (size.isPresent()) {
                totalItems = size.get();
            }
        }

        List<RequestWithProposalsDto> requests = queryFactory.selectFrom(request)
                .where(request.requester.id.notIn(userId))
                .orderBy(request.created.desc())
                .limit(totalItems)
                .offset(fromExist)
                .fetch()
                .stream()
                .map(requestMapper::toRequestWithProposalDto)
                .collect(Collectors.toList());
        requests.forEach(rQ -> rQ.setItems(getItemsDto(rQ.getId())));

        return requests.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private List<ItemDto> getItemsDto(Long requestId) {
        JPAQuery<Item> query = new JPAQuery<>(em);
        QItem qItem = QItem.item;
        return query.from(qItem)
                .where(qItem.requestId.eq(requestId))
                .fetch()
                .stream()
                .map(ItemMapper.itemMapper::toDto)
                .collect(Collectors.toList());
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

    private void checkFromAndSize(Optional<Integer> from, Optional<Integer> size) {
        if (from.isPresent() && from.get() < 0) {
            throw new BadRequestException("Start position must be >= 0, not " + from);
        }
        if (size.isPresent() && size.get() <= 0) {
            throw new BadRequestException("Size must be >= 0, not " + size);
        }
    }
}