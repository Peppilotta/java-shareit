package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.InRepositoryRequestService;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceTest {
    LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private UserService userService;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService =
            new InRepositoryRequestService(requestRepository, userRepository, requestMapper, userService);

    @Test
    void save_WrongUserId() {
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        Long userId = 100L;
        RequestDto requestDto = createRequestDto();
        when(userRepository.existsById(anyLong())).thenReturn(false);
        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> requestService.save(userId, requestDto));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("User with id=" + userId + " not exists."));
    }

    @Test
    void save_StandardBehavior() {
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        ReflectionTestUtils.setField(requestService, "requestMapper", requestMapper);
        ReflectionTestUtils.setField(requestService, "requestRepository", requestRepository);
        ReflectionTestUtils.setField(requestService, "userService", userService);

        User user = createUser();
        UserDto userDto = createUserDto();
        Request request = createRequest();
        RequestDto requestDto = createRequestDto();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestMapper.toRequest(any())).thenReturn(request);
        when(requestMapper.toDto(any())).thenReturn(requestDto);
        when(requestRepository.save(any())).thenReturn(request);

        RequestDto expectedRequestDto = requestService.save(user.getId(), requestDto);

        assertThat(expectedRequestDto.getId(), equalTo(requestDto.getId()));
        assertThat(expectedRequestDto.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(expectedRequestDto.getCreated(), equalTo(requestDto.getCreated()));
        assertThat(expectedRequestDto.getRequester().getId(), equalTo(userDto.getId()));
        assertThat(expectedRequestDto.getRequester().getName(), equalTo(userDto.getName()));
        assertThat(expectedRequestDto.getRequester().getEmail(), equalTo(userDto.getEmail()));
    }

    private Request createRequest() {
        return Request.builder()
                .id(33L)
                .description("нужен аэрогриль")
                .requester(createUser())
                .created(dateTime.minusDays(3))
                .build();
    }

    private RequestDto createRequestDto() {
        return RequestDto.builder()
                .id(33L)
                .description("нужен аэрогриль")
                .requester(createUserDto())
                .created(dateTime.minusDays(3))
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(33L)
                .name("Alex")
                .email("azvarich@rubytech.ru")
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(33L)
                .name("Alex")
                .email("azvarich@rubytech.ru")
                .build();
    }
}