package ru.practicum.shareit.user.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.user.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    @AfterMapping
    default void linkItems(@MappingTarget User user) {
        user.getItems().forEach(item -> item.setOwner(user));
    }

    @AfterMapping
    default void linkComments(@MappingTarget User user) {
        user.getComments().forEach(c -> c.setAuthor(user));
    }
}