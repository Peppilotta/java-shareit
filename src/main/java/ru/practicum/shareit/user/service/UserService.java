package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.FindDuplicateException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        log.info("Create request for user {}", user);
        avoidConflict(0, user.getEmail());
        return userStorage.createUser(user);
    }

    public User update(long id, Map<String, Object> updates) {
        log.info("Update request for user with id={}", id);
        checkUserExistence(id);
        if (updates.containsKey("email")) {
            String emailFromUpdate = String.valueOf(updates.get("email"));
            if (!Objects.equals(emailFromUpdate.trim().toLowerCase(),
                    userStorage.getUser(id).getEmail().trim().toLowerCase())) {
                avoidConflict(id, emailFromUpdate);
            }
        }
        return userStorage.updateUser(id, updates);
    }

    public List<User> getUsers() {
        log.info("GET request - all users");
        return userStorage.getUsers();
    }

    public User getUser(long id) {
        log.info("GET request - user id={} ", id);
        checkUserExistence(id);
        return userStorage.getUser(id);
    }

    public User deleteUser(long id) {
        log.info("Delete request - user id={} ", id);
        checkUserExistence(id);
        User deletedUser = userStorage.getUser(id);
        userStorage.deleteUser(id);
        log.info("User deleted: {} ", deletedUser.toString());
        return deletedUser;
    }

    private void checkUserExistence(long id) {
        if (!userStorage.checkUserExistence(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void avoidConflict(long id, String email) {
        long idUserWithEmail = userStorage.checkEmailExistence(email);
        if ((idUserWithEmail > 0 && id == 0) || (idUserWithEmail > 0 && idUserWithEmail != id)) {
            throw new FindDuplicateException("User with email=" + email + " already exists.");
        }
    }
}
