package ru.practicum.shareit.item.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    private final CommentMapper commentMapper;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    @PersistenceContext
    EntityManager em;

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("New request");
        log.info("Create request for itemDto={} from userId={} ", itemDto, userId);
        checkItemIsAvailable(itemDto);
        checkUserExists(userId);
        Optional<User> user = userRepository.findById(userId);
        itemDto.setOwner(userMapper.toDto(user.orElse(null)));
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        return itemMapper.toDto(item);
    }

    public ItemDto updateItem(Long userId, Long id, Map<String, Object> updates) {
        log.debug("New request");
        log.info("Update request for item with id={} from userId={} ", id, userId);
        checkUserExists(userId);
        checkItemExists(id);
        checkItemOwnerId(userId, id);
        Item item = itemMapper.toItem(this.getItem(userId, id));
        if (updates.containsKey("name")) {
            item.setName(String.valueOf(updates.get("name")));
            log.debug("Name updated");
        }
        if (updates.containsKey("description")) {
            item.setDescription(String.valueOf(updates.get("description")));
            log.debug("Description updated");
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.parseBoolean(String.valueOf(updates.get("available"))));
            log.debug("Available updated");
        }
        itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    public ItemDto getItem(Long userId, Long id) {
        log.debug("New request");
        log.info("Get request getItemById from userId={} for item with id={}", userId, id);
        checkUserExists(userId);
        checkItemExists(id);
        Item item = itemRepository.findById(id).get();
        ItemDto itemDto = itemMapper.toDto(item);
        log.debug("Get request getItemById - map comments to ItemDto");
        itemDto.setComments(commentMapper.map(item.getItemComments()));
        if (Objects.equals(userId, item.getOwner().getId())) {
            log.debug("Get request getItemById - setLastBooking");
            itemDto.setLastBooking(getLastBookingForItem(id));
            log.debug("Get request getItemById - setNextBooking");
            itemDto.setNextBooking(getFutureBookingFotItem(id));
        }
        return itemDto;
    }

    public List<ItemDto> getItems(Long userId, Optional<Integer> from, Optional<Integer> size) {
        checkFromAndSize(from, size);
        checkUserExists(userId);
        QItem qItem = QItem.item;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        long totalItems = itemRepository.count() + 1;
        Integer fromExist = 0;
        if (from.isPresent()) {
            fromExist = from.get();
            int first = fromExist >= 1 ? --fromExist : fromExist;
            if (size.isPresent()) {
                totalItems = size.get();
            }
        }
        List<ItemDto> itemsDto = queryFactory.selectFrom(qItem)
                .where(qItem.owner.id.in(userId))
                .orderBy(qItem.id.asc())
                .limit(totalItems)
                .offset(fromExist)
                .fetch()
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemsDto) {
            Long itemDtoId = itemDto.getId();
            if (Objects.equals(userId, itemDto.getOwner().getId())) {
                itemDto.setLastBooking(getLastBookingForItem(itemDtoId));
                itemDto.setNextBooking(getFutureBookingFotItem(itemDtoId));
            }
        }
        return itemsDto.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public ItemDto deleteItem(Long userId, Long id) {
        log.debug("New request");
        log.info("Delete request for itemId={} from user with id={}", id, userId);
        checkItemExists(id);
        Item item = itemRepository.findById(id).get();
        ItemDto itemDto = itemMapper.toDto(item);
        itemRepository.delete(item);
        log.debug("Item deleted");
        return itemDto;
    }

    public List<ItemDto> searchItem(Long userId, String keyWord, Optional<Integer> from, Optional<Integer> size) {
        log.debug("New request");
        log.info("Get request for item owned by user with id={} and label={}", userId, keyWord);
        checkFromAndSize(from, size);
        if (keyWord.trim().isEmpty()) {
            return new ArrayList<>();
        }
        checkUserExists(userId);

        String query = "%" + keyWord.trim().toLowerCase() + "%";
        List<ItemDto> itemsDto = itemRepository.findByNameOrDescription(query)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        int totalItems = (int) itemRepository.count() + 1;
        Integer fromExist = 0;
        if (from.isPresent()) {
            fromExist = from.get();
            int first = fromExist >= 1 ? --fromExist : fromExist;
            if (size.isPresent()) {
                totalItems = size.get() + first - 1;
            }
           return itemsDto.subList(first, totalItems);
        }
        for (ItemDto itemDto : itemsDto) {
            Long itemDtoId = itemDto.getId();
            if (Objects.equals(userId, itemDto.getOwner().getId())) {
                itemDto.setLastBooking(getLastBookingForItem(itemDtoId));
                itemDto.setNextBooking(getFutureBookingFotItem(itemDtoId));
            }
        }

        return itemsDto;
    }

    public CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto) {
        log.debug("New request");
        log.info("Create comment request for itemId={} from userId={} and comment={}", itemId, userId, commentDto);
        checkUserExists(userId);
        checkCommentEmpty(commentDto);
        checkItemExists(itemId);
        Optional<User> user = userRepository.findById(userId);
        User author = user.orElse(null);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        Item item = itemOptional.orElse(null);
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore
                (itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadRequestException
                    ("error while trying to add comment to item which hasn't finished booking by user");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.debug("new comment for item: {} created: {}", itemId, comment);
        return commentMapper.toDto(comment);
    }

    private ItemBookingDto getLastBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.searchByItemIdAndEndBeforeDate(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        log.debug("Bookings for itemId= {} in the past = {}", itemId, bookings.size());
        if (bookings.isEmpty()) {
            return null;
        }
        Comparator<Booking> byDateEnd = Comparator.comparing(Booking::getEnd).reversed();
        List<Booking> bookingsSorted = bookings.stream()
                .sorted(byDateEnd)
                .limit(1)
                .collect(Collectors.toList());
        Booking booking = bookingsSorted.get(0);
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    private ItemBookingDto getFutureBookingFotItem(Long itemId) {
        List<Booking> bookings = bookingRepository.searchByItemIdAndStartAfterDate(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        log.debug("Bookings for itemId={} in the future = {}", itemId, bookings.size());
        if (bookings.isEmpty()) {
            return null;
        }
        Comparator<Booking> byDateStart = Comparator.comparing(Booking::getStart);
        List<Booking> bookingsOrdered = bookings.stream()
                .sorted(byDateStart)
                .limit(1)
                .collect(Collectors.toList());
        Booking booking = bookingsOrdered.get(0);
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId());
    }

    public Item map(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        log.debug("item with id: {} requested, returned result: {}", id, item);
        return item.orElseThrow(() -> new EntityNotFoundException("item with id: " + id + " doesn't exists"));
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

    private void checkFromAndSize(Optional<Integer> from, Optional<Integer> size) {
        if (from.isPresent() && from.get() < 0) {
            throw new BadRequestException("Start position must be >= 0, not " + from);
        }
        if (size.isPresent() && size.get() <= 0) {
            throw new BadRequestException("Size must be >= 0, not " + size);
        }
    }
}