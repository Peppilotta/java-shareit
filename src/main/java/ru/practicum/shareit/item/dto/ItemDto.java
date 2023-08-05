package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {

    private long id;

    @NotBlank(message = "Item name absent")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Item description absent")
    @Size(max = 255)
    private String description;

    @NotNull(message = "Item availability absent")
    private boolean available;

    private Long requestId;

    private ItemBookingDto lastBooking;

    private ItemBookingDto nextBooking;

    private Set<CommentDto> comments;

    private UserDto owner;
}