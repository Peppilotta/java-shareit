package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 9, 0, 0, 0);

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    void setJsonBookingDto() throws Exception {
        JsonContent<BookingDto> result = jsonBookingDto.write(createBookingDto());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(33);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-08-07T09:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-08-17T09:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");

        assertThat(result).extractingJsonPathValue("$.booker.id").isEqualTo(33);
        assertThat(result).extractingJsonPathValue("$.booker.name").isEqualTo("Sergey");
        assertThat(result).extractingJsonPathValue("$.booker.email").isEqualTo("serg@micromash.ru");
        assertThat(result).extractingJsonPathValue("$.booker.registrationDate").isEqualTo("2023-08-02T09:00:00");

        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(33);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("drill");
        assertThat(result).extractingJsonPathValue("$.item.description").isEqualTo("hammer drill");
        assertThat(result).extractingJsonPathValue("$.item.requestId").isEqualTo(99);
        assertThat(result).extractingJsonPathValue("$.item.owner.id").isEqualTo(66);
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("Maxim");
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("Maxim");
        assertThat(result).extractingJsonPathValue("$.item.owner.email").isEqualTo("max@micromash.ru");
        assertThat(result).extractingJsonPathValue("$.item.owner.registrationDate").isEqualTo("2023-08-01T09:00:00");
        assertThat(result).extractingJsonPathValue("$.item.lastBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathValue("$.item.lastBooking.bookerId").isEqualTo(22);
        assertThat(result).extractingJsonPathValue("$.item.nextBooking.id").isEqualTo(44);
        assertThat(result).extractingJsonPathValue("$.item.nextBooking.bookerId").isEqualTo(55);
        assertThat(result).extractingJsonPathValue("$.item.comments[0].id").isEqualTo(77);
        assertThat(result).extractingJsonPathValue("$.item.comments[0].authorId").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.item.comments[0].text").isEqualTo("useful");
        assertThat(result).extractingJsonPathValue("$.item.comments[0].authorName").isEqualTo("Danila");
        assertThat(result).extractingJsonPathValue("$.item.comments[0].created").isEqualTo("2023-08-09T09:00:00");
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(33L)
                .name("Sergey")
                .email("serg@micromash.ru")
                .registrationDate(dateTime.minusDays(10))
                .build();
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

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(33L)
                .item(createItemDto())
                .booker(createUserDto())
                .start(dateTime.minusDays(5))
                .end(dateTime.plusDays(5))
                .status(BookingStatus.REJECTED)
                .build();
    }
}