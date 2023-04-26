package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class UserStorageInMemory implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long count = 0;

    @Override
    public User createUser(User user) {
        user.setId(++count);
        users.put(count, user);
        return user;
    }

    @Override
    public User updateUser(long id, Map<String, Object> updates) {
        User updatedUser = users.get(id);
        if (updates.containsKey("name")) {
            updatedUser.setName(String.valueOf(updates.get("name")));
        }
        if (updates.containsKey("email")) {
            updatedUser.setEmail(String.valueOf(updates.get("email")));
        }
        return updatedUser;
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User deleteUser(long id) {
        User deletedUser = users.get(id);
        users.remove(id);
        return deletedUser;
    }

    @Override
    public boolean checkUserExistence(long id) {
        return users.containsKey(id);
    }

    @Override
    public long checkEmailExistence(String email) {
        List<User> usersIdWithSameEmail = users.values().stream()
                .filter(user -> Objects.equals(user.getEmail().toLowerCase(), email.trim().toLowerCase()))
                .collect(Collectors.toList());
        if (usersIdWithSameEmail.isEmpty()) {
            return 0;
        } else {
            return usersIdWithSameEmail.get(0).getId();
        }
    }
}