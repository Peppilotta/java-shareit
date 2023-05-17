package ru.practicum.shareit.user.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.user.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromUserUpdateDto(UserUpdateDto userUpdateDto, @MappingTarget User user);

    @AfterMapping
    default void linkItems(@MappingTarget User user) {
        user.getItems().forEach(item -> item.setOwner(user));
    }

    @AfterMapping
    default void linkComments(@MappingTarget User user) {
        user.getComments().forEach(Comment -> Comment.setAuthor(user));
    }
}