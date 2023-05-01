package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public User toUser(UserDto userDto) {
        return new User().toBuilder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public UserDto toDto(User user) {
        return new UserDto().toBuilder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}