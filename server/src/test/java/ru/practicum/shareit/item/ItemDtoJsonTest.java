package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoJsonTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    @Test
    void setJsonItemDto() throws Exception {
        JsonContent<ItemDto> result = jsonItemDto.write(createItemDto());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(33);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("hammer drill");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(99);

        assertThat(result).extractingJsonPathValue("$.owner.id").isEqualTo(66);
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo("Maxim");
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo("Maxim");
        assertThat(result).extractingJsonPathValue("$.owner.email").isEqualTo("max@micromash.ru");
        assertThat(result).extractingJsonPathValue("$.owner.registrationDate").isEqualTo("2023-08-01T09:00:00");

        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathValue("$.lastBooking.bookerId").isEqualTo(22);

        assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(44);
        assertThat(result).extractingJsonPathValue("$.nextBooking.bookerId").isEqualTo(55);

        assertThat(result).extractingJsonPathValue("$.comments[0].id").isEqualTo(77);
        assertThat(result).extractingJsonPathValue("$.comments[0].authorId").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.comments[0].text").isEqualTo("useful");
        assertThat(result).extractingJsonPathValue("$.comments[0].authorName").isEqualTo("Danila");
        assertThat(result).extractingJsonPathValue("$.comments[0].created").isEqualTo("2023-08-09T09:00:00");
    }

    private UserDto createUserDtoOwner() {
        return UserDto.builder()
                .id(66L)
                .name("Maxim")
                .email("max@micromash.ru")
                .registrationDate(dateTime.minusDays(11))
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(33L)
                .name("drill")
                .description("hammer drill")
                .owner(createUserDtoOwner())
                .available(true)
                .requestId(99L)
                .lastBooking(new ItemBookingDto(11L, 22L))
                .nextBooking(new ItemBookingDto(44L, 55L))
                .comments(Set.of(createCommentDto()))
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(77L)
                .text("useful")
                .authorId(100L)
                .authorName("Danila")
                .created(dateTime.minusDays(3))
                .build();
    }
}