package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {

    private Long id;

    @NotNull
    private String name;

    @NotBlank
    @Email
    private String email;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationDate;
}