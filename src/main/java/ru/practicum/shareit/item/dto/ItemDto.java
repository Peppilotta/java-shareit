package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
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

    private boolean available = true;

    private long necessity = 0;
}
