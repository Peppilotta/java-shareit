package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestInputDto {

    @NotEmpty(message = "Type something about you needs")
    private String description;
}