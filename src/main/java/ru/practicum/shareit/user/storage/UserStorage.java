package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    User getUser(long id);

    List<User> getUsers();

    User deleteUser(long id);

    boolean checkUserExistence(long id);

    long getUserIdWithSuchEmail(String email);
}