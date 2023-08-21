package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService = new ItemService(itemRepository, userRepository,
            commentRepository, bookingRepository, commentMapper, itemMapper, userMapper);

    @Test
    void createItem_StandardBehavior() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(any())).thenReturn(item);
        when(itemMapper.toDto(any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.createItem(owner.getId(), itemDto);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void createItem_ItemNotAvailable() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = createUser().getId();
        ItemDto itemDto = createItemDtoWithoutAvailable();

        BadRequestException badRequestException
                = assertThrows(BadRequestException.class, () -> itemService.createItem(userId, itemDto));
        assertThat(badRequestException.getMessage(), equalTo("Item field AVAILABLE is absent"));
    }

    @Test
    void createItem_WrongUser() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = 100L;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> itemService.createItem(userId, createItemDto()));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("User with id=" + userId + " not exists."));
    }

    @Test
    void updateItem_ItemIdNotExist() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = 100L;
        Long itemId = 100L;
        Map<String, Object> updates = Map.of("name", "супервелотренажёр");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(false);

        ItemDoesNotExistException itemDoesNotExistException
                = assertThrows(ItemDoesNotExistException.class, () -> itemService.updateItem(userId, itemId, updates));
        assertThat(itemDoesNotExistException.getMessage(), equalTo("Item with id=" + itemId + " not exists."));
    }

    @Test
    void updateItem_ItemIdExists_NotOwner() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);

        Long userId = 100L;
        Item item = createItem();
        Long itemId = item.getId();
        Map<String, Object> updates = Map.of("name", "супервелотренажёр");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotOwnerException notOwnerException
                = assertThrows(NotOwnerException.class, () -> itemService.updateItem(userId, itemId, updates));

        assertThat(notOwnerException.getMessage(),
                equalTo("User with id=" + userId + "  is not owner of item with id=" + itemId));
    }

    @Test
    void updateItem_ItemIdExist_ByOwner_NewName() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        Map<String, Object> updates = Map.of("name", "супервелотренажёр");

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        itemDto.setName(String.valueOf(updates.get("name")));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemMapper.toItem(any())).thenReturn(item);
        when(itemMapper.toDto(any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.updateItem(owner.getId(), itemDto.getId(), updates);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void updateItem_ItemIdExist_ByOwner_NewDescription() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        Map<String, Object> updates = Map.of("description", "10 скоростей");

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        itemDto.setName(String.valueOf(updates.get("description")));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemMapper.toItem(any())).thenReturn(item);
        when(itemMapper.toDto(any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.updateItem(owner.getId(), itemDto.getId(), updates);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void updateItem_ItemIdExist_ByOwner_NewAvailable() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        Map<String, Object> updates = Map.of("available", false);

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        itemDto.setAvailable((boolean) updates.get("available"));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemMapper.toItem(any())).thenReturn(item);
        when(itemMapper.toDto(any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.updateItem(owner.getId(), itemDto.getId(), updates);

        assertThat(expectedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    @Test
    void getItem_StandardBehavior() {
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "userMapper", userMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        User owner = createOwner();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        Long itemId = itemDto.getId();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemMapper.toDto(any())).thenReturn(itemDto);

        ItemDto expectedItemDto = itemService.getItem(owner.getId(), itemId);

        assertThat(expectedItemDto.getId(), equalTo(itemId));
        assertThat(expectedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(expectedItemDto.isAvailable(), equalTo(itemDto.isAvailable()));
    }

    private Item createItem() {
        return Item.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .owner(createOwner())
                .requestId(100L)
                .owner(createOwner())
                .build();
    }

    private ItemDto createItemDtoWithoutAvailable() {
        return ItemDto.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .owner(createOwnerDto())
                .requestId(100L)
                .owner(createOwnerDto())
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(33L)
                .name("велотренажёр")
                .description("очень тяжёлый")
                .available(true)
                .owner(createOwnerDto())
                .requestId(100L)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(33L)
                .name("Alex")
                .email("azvarich@rubytech.ru")
                .build();
    }

    private User createOwner() {
        return User.builder()
                .id(99L)
                .name("Tigran")
                .email("tigran@rubytech.ru")
                .build();
    }

    private UserDto createOwnerDto() {
        return UserDto.builder()
                .id(99L)
                .name("Tigran")
                .email("tigran@rubytech.ru")
                .build();
    }
}