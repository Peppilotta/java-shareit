package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateDto implements Serializable {
    private final Long id;
    private final Set<ItemDto> items;
    private final Set<CommentDto> Comments;
    private final String name;
    private final String email;
    private final LocalDate registrationDate;
}