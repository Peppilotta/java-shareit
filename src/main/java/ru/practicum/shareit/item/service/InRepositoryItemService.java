package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class InRepositoryItemService implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    private final CommentMapper commentMapper;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Create request for itemDto {}", itemDto);
        checkItemIsAvailable(itemDto);
        checkUserExists(userId);
        itemDto.setOwner(userMapper.toDto(userRepository.findById(userId).get()));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, Map<String, Object> updates) {
        log.info("Update request for item with id={}", id);
        checkUserExists(userId);
        checkItemExists(id);
        checkItemOwnerId(userId, id);
        Item item = itemMapper.toItem(this.getItem(userId, id));
        if (updates.containsKey("name")) {
            item.setName(String.valueOf(updates.get("name")));
        }
        if (updates.containsKey("description")) {
            item.setDescription(String.valueOf(updates.get("description")));
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.parseBoolean(String.valueOf(updates.get("available"))));
        }
        itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto getItem(Long userId, Long id) {
        log.info("Get request for item with id={}", id);
        checkUserExists(userId);
        checkItemExists(id);
        Item item = itemRepository.findById(id).get();
        ItemDto itemDto = itemMapper.toDto(item);
        itemDto.setComments(commentMapper.map(item.getItemComments()));
        itemDto.setLastBooking(getLastBookingForItem(id));
        itemDto.setNextBooking(getFutureBookingFotItem(id));
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.info("Get request for items of user with id={}", userId);
        checkUserExists(userId);
        List<Item> items = new ArrayList<>(itemRepository.findByOwnerId(userId));
        if (items.isEmpty()) {
            return new ArrayList<ItemDto>();
        }
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemsDto) {
            Long itemDtoId = itemDto.getId();
            itemDto.setLastBooking(getLastBookingForItem(itemDtoId));
            itemDto.setNextBooking(getFutureBookingFotItem(itemDtoId));
        }
        return itemsDto;
    }

    @Override
    public ItemDto deleteItem(Long userId, Long id) {
        checkItemExists(id);
        Item item = itemRepository.findById(id).get();
        ItemDto itemDto = itemMapper.toDto(item);
        itemRepository.delete(item);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String keyWord) {
        log.info("Get request for item owned by user with id={}", userId);
        if (keyWord.trim().isEmpty()) {
            return new ArrayList<>();
        }
        checkUserExists(userId);
        String query = "%" + keyWord.trim().toLowerCase() + "%";
        return new ArrayList<>(itemRepository.findByNameOrDescription(query)
                .stream()
                .filter(Objects::nonNull)
                .map(itemMapper::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
    }

    @Override
    public Comment saveComment(Long itemId, Long userId, CommentDto commentDto) {
        checkUserExists(userId);
        checkCommentEmpty(commentDto);
        checkItemExists(itemId);
        User author = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).get();
        if (bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore
                (itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            Comment comment = commentMapper.toComment(commentDto);
            comment.setAuthor(author);
            comment.setItem(item);
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            log.debug("new comment for item: {} created: {}", itemId, comment);

            return comment;
        } else {
            throw new BadRequestException
                    ("error while trying to add comment to item which hasn't  finished booking by user");
        }
    }

    private ItemBookingDto getLastBookingForItem(Long itemId) {
        List<Booking> bookings = new LinkedList<>(bookingRepository
                .searchByItemIdAndEndBeforeDate(itemId, LocalDateTime.now(), "APPROVED"));
        if (bookings.isEmpty()) {
            System.out.println("getLastBookingForItem = null");
            return null;
        }
        System.out.println("getLastBookingForItem");
        for (Booking booking : bookings) {
            System.out.println("id=" + booking.getId().toString() + ", bookerId=" + booking.getBooker().getId());
        }
        Booking booking = bookings.get(0);
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    private ItemBookingDto getFutureBookingFotItem(Long itemId) {
        List<Booking> bookings = new LinkedList<>(bookingRepository
                .searchByItemIdAndStartAfterDate(itemId, LocalDateTime.now(), "APPROVED"));
        if (bookings.isEmpty()) {
            System.out.println("getFutureBookingFotItem = null");

            return null;
        }
        System.out.println("getFutureBookingFotItem");
        for (Booking booking : bookings) {
            System.out.println("id=" + booking.getId().toString() + ", bookerId=" + booking.getBooker().getId());
        }
        Booking booking = bookings.get(0);
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    public Item map(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        log.debug("item with id: {} requested, returned result: {}", id, item);
        return item.orElseThrow(() -> new EntityNotFoundException("item with id: " + id + " doesn't exists"));
    }

    private ItemDto getItemDtoWithLastAndNextBookings(ItemDto itemDto, List<Booking> bookings) {
        if (bookings.size() > 1) {
            Long lastBookingId = getBookingId(bookings, 0);
            Long lastBookerId = getBookerId(bookings, 0);
            itemDto.setLastBooking(new ItemBookingDto(lastBookingId, lastBookerId));

            Long nextBookingId = getBookingId(bookings, 1);
            Long nextBookerId = getBookerId(bookings, 1);
            itemDto.setNextBooking(new ItemBookingDto(nextBookingId, nextBookerId));
        }

        return itemDto;
    }

    private Long getBookingId(List<Booking> bookings, int rowNumber) {
        return bookings.get(rowNumber).getId();
    }

    private Long getBookerId(List<Booking> bookings, int rowNumber) {
        return bookings.get(rowNumber).getBooker().getId();
    }

    private void checkUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ItemDoesNotExistException("User with id=" + id + " not exists.");
        }
    }

    private void checkItemIsAvailable(ItemDto item) {
        if (!item.isAvailable()) {
            throw new BadRequestException("Item field AVAILABLE is absent");
        }
    }

    private void checkItemExists(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemDoesNotExistException("Item with id=" + id + " not exists.");
        }
    }

    private void checkCommentEmpty(CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new BadRequestException("Comment should not be empty");
        }
    }

    private void checkItemOwnerId(Long userId, Long id) {
        if (!Objects.equals(userId, itemRepository.findById(id).get().getOwner().getId())) {
            throw new NotOwnerException("User with id=" + userId + "  is not owner of item with id=" + id);
        }
    }
}