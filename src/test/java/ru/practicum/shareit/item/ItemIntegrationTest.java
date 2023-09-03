package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(properties = {"db.name=test"}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"classpath:./schema.sql", "classpath:./DataForTests.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserMapper userMapper;
    private final UserService userService = new UserService(userRepository, userMapper);
    private final ItemService itemService = new ItemService(itemRepository, userRepository,
            commentRepository, bookingRepository, commentMapper, itemMapper, userMapper);

    @Test
    void getItem_StandardBehavior() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);

        UserDto userDto = userService.getUserDto(1L);
        ItemDto itemDto = itemService.getItem(1L, 1L);

        assertThat(userDto.getName(), equalTo("Dimetrios1"));
        assertThat(itemDto.getName(), equalTo("screen"));
    }

    @Test
    void searchItem_StandardBehavior() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);

        List<ItemDto> itemsDto = itemService.searchItem(1L, "бытОВой", 0, 5);

        assertThat(itemsDto, hasSize(2));
        assertThat(itemsDto.get(0).getName(), equalTo("микроскоп"));
        assertThat(itemsDto.get(1).getName(), equalTo("кислородный аппарат"));
    }
}