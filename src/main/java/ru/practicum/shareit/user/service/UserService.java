package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto create(UserDto user) {
        log.info("New request");
        log.info("Create request for user {}", user);
        return userMapper.toDto(userRepository.save(userMapper.toUser(user)));
    }

    public UserDto update(Long id, Map<String, Object> updates) {
        log.info("New request");
        log.info("Update request for user with id={}", id);
        checkUserExistence(id);
        User user = userRepository.findById(id).get();
        if (updates.containsKey("email")) {
            user.setEmail(String.valueOf(updates.get("email")));
        }
        if (updates.containsKey("name")) {
            user.setName(String.valueOf(updates.get("name")));
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsers() {
        log.info("New request");
        log.info("GET request - all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        log.info("New request");
        log.info("GET request - user id={} ", id);
        checkUserExistence(id);
        return userMapper.toDto(userRepository.findById(id).get());
    }

    public UserDto deleteUser(Long id) {
        log.info("New request");
        log.info("Delete request - user id={} ", id);
        checkUserExistence(id);
        User deletedUser = userRepository.findById(id).get();
        userRepository.deleteById(id);
        log.info("User deleted: {} ", deletedUser);
        return userMapper.toDto(deletedUser);
    }

    private void checkUserExistence(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }
}