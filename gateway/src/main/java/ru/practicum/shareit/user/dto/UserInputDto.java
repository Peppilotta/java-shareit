package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {

    @NotBlank(message = "Type username")
    private String name;

    @NotBlank(message = "Type email")
    @Email(message = "Type email")
    private String email;
}