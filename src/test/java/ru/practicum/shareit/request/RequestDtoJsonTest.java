package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RequestDtoJsonTest {

    LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Autowired
    private JacksonTester<RequestDto> jsonRequestDto;

    @Test
    void setJsonRequestDto() throws Exception {
        JsonContent<RequestDto> result = jsonRequestDto.write(createRequestDto());
        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(33);
        assertThat(result)
                .extractingJsonPathStringValue("$.created").isEqualTo("2023-08-09T09:00:00");
        assertThat(result)
                .extractingJsonPathValue("$.requester.id").isEqualTo(33);
        assertThat(result)
                .extractingJsonPathValue("$.requester.name").isEqualTo("Sergey");
        assertThat(result)
                .extractingJsonPathValue("$.requester.email").isEqualTo("serg@micromash.ru");
        assertThat(result)
                .extractingJsonPathValue("$.requester.registrationDate").isEqualTo("2023-08-02T09:00:00");
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(33L)
                .name("Sergey")
                .email("serg@micromash.ru")
                .registrationDate(dateTime.minusDays(10))
                .build();
    }

    private RequestDto createRequestDto() {
        return RequestDto.builder()
                .id(33L)
                .requester(createUserDto())
                .created(dateTime.minusDays(3))
                .build();
    }
}