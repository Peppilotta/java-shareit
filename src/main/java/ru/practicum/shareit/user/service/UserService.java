package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.FindDuplicateException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    private final UserMapper userMapper;

    public UserService(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        log.info("Create request for user {}", userDto);
        validateUniqueEmail(0, userDto.getEmail());
        User user = userStorage.createUser(userMapper.toUser(userDto));
        return userMapper.toDto(user);
    }

    public UserDto update(long id, Map<String, Object> updates) {
        log.info("Update request for user with id={}", id);
        checkUserExistence(id);
        User user = userStorage.getUser(id);
        if (updates.containsKey("email")) {
            String emailFromUpdate = String.valueOf(updates.get("email"));
            if (!Objects.equals(emailFromUpdate.trim().toLowerCase(),
                    user.getEmail().trim().toLowerCase())) {
                validateUniqueEmail(id, emailFromUpdate);
            }
            user.setEmail(String.valueOf(updates.get("email")));
        }
        if (updates.containsKey("name")) {
            user.setName(String.valueOf(updates.get("name")));
        }

        return userMapper.toDto(userStorage.updateUser(user));
    }

    public List<UserDto> getUsers() {
        log.info("GET request - all users");
        return userStorage.getUsers()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(long id) {
        log.info("GET request - user id={} ", id);
        checkUserExistence(id);
        return userMapper.toDto(userStorage.getUser(id));
    }

    public UserDto deleteUser(long id) {
        log.info("Delete request - user id={} ", id);
        checkUserExistence(id);
        User deletedUser = userStorage.getUser(id);
        userStorage.deleteUser(id);
        log.info("User deleted: {} ", deletedUser.toString());
        return userMapper.toDto(deletedUser);
    }

    private void checkUserExistence(long id) {
        if (!userStorage.checkUserExistence(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void validateUniqueEmail(long id, String email) {
        long userId = userStorage.getUserIdUsingEmail(email);
        if ((userId > 0 && id == 0) || (userId > 0 && userId != id)) {
            throw new FindDuplicateException("User with email=" + email + " already exists.");
        }
    }
}
