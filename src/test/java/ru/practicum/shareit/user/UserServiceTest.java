package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService = new UserService(userRepository, userMapper);

    @Test
    void create_ShouldReturnSameEntity() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        User user = createUser();
        UserDto userDto = createUserDto();
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userMapper.toUser(userDto)).thenReturn(user);

        UserDto expectedUserDto = userService.create(userDto);

        assertThat(expectedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUser_WrongIdShouldThrowException() {

        Long userId = 100L;
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        when(userRepository.existsById(anyLong())).thenReturn(false);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> userService.getUser(userId));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("User with id=" + userId + " not exists."));
    }

    @Test
    void getUser_StandardBehavior() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        User user = createUser();
        Optional<User> optionalUser = Optional.of(user);
        UserDto userDto = createUserDto();

        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        UserDto expectedUserDto = userService.getUserDto(33L);

        assertThat(expectedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUsers_StandardBehavior() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        User user = createUser();
        List<User> expectedUsers = List.of(user);
        UserDto userDto = createUserDto();
        List<UserDto> usersDto = List.of(userDto);

        when(userRepository.findAll()).thenReturn(expectedUsers);
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> expectedUserDTOs = userService.getUsers();

        assertThat(expectedUserDTOs.size(), equalTo(usersDto.size()));
        assertThat(expectedUserDTOs.get(0).getId(), equalTo(usersDto.get(0).getId()));
        assertThat(expectedUserDTOs.get(0).getName(), equalTo(usersDto.get(0).getName()));
        assertThat(expectedUserDTOs.get(0).getEmail(), equalTo(usersDto.get(0).getEmail()));
    }

    @Test
    void update_WrongIdShouldThrowException() {

        Long userId = 100L;
        Map<String, Object> updates = Map.of("name", "Peppy", "email", "peppyLS@mail.ru");
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        when(userRepository.existsById(anyLong())).thenReturn(false);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> userService.update(userId, updates));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("User with id=" + userId + " not exists."));
    }

    @Test
    void delete_WrongIdShouldThrowException() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        Long userId = 100L;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> userService.deleteUser(userId));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("User with id=" + userId + " not exists."));
    }

    @Test
    void update_StandardBehavior_ChangeName() {

        Map<String, Object> updates = Map.of("name", "Peppy");
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();
        User updatedUser = createUser();
        updatedUser.setName(String.valueOf(updates.get("name")));
        UserDto updatedUserDto = createUserDto();
        updatedUserDto.setName(String.valueOf(updates.get("name")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.toDto(any())).thenReturn(updatedUserDto);

        UserDto expectedUserDto = userService.update(33L, updates);

        assertThat(expectedUserDto.getId(), equalTo(updatedUserDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(updatedUserDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(updatedUserDto.getEmail()));
    }

    @Test
    void update_StandardBehavior_ChangeEmail() {
        Map<String, Object> updates = Map.of("email", "peppyLS@mail.ru");
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();
        User updatedUser = createUser();
        updatedUser.setName(String.valueOf(updates.get("email")));
        UserDto updatedUserDto = createUserDto();
        updatedUserDto.setName(String.valueOf(updates.get("email")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.toDto(any())).thenReturn(updatedUserDto);

        UserDto expectedUserDto = userService.update(33L, updates);

        assertThat(expectedUserDto.getId(), equalTo(updatedUserDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(updatedUserDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(updatedUserDto.getEmail()));
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
