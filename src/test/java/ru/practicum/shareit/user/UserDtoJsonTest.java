package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoJsonTest {

    LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Test
    void setJsonRequestDto() throws Exception {
        JsonContent<UserDto> result = jsonUserDto.write(createUserDto());
        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(33);
        assertThat(result)
                .extractingJsonPathStringValue("$.name").isEqualTo("Sergey");
        assertThat(result)
                .extractingJsonPathStringValue("$.email").isEqualTo("serg@micromash.ru");
        assertThat(result)
                .extractingJsonPathStringValue("$.registrationDate").isEqualTo("2023-08-02T09:00:00");
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(33L)
                .name("Sergey")
                .email("serg@micromash.ru")
                .registrationDate(dateTime.minusDays(10))
                .build();
    }
}