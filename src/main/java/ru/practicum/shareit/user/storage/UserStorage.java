package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User createUser(User user);

    User updateUser(long id, Map<String, Object> updates);

    User getUser(long id);

    List<User> getUsers();

    User deleteUser(long id);

    boolean checkUserExistence(long id);

    long checkEmailExistence(String email);
}