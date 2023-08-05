package ru.practicum.shareit.comment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class CommentDto {
    private final Long id;
    private final String text;
    private final Long authorId;
    private final String authorName;
    private final LocalDateTime created;
}