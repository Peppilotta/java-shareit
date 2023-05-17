package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

/*@Mapper(componentModel = "spring")
@Primary*/
public abstract class BookingMapperWithRepositories implements BookingMapper {
    @Autowired
    protected ItemService itemService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected ItemMapper itemMapper;

    @Autowired
    @Qualifier("delegate")
    private BookingMapper delegate;

    @Override
    public Booking toBooking(BookingDtoWithId bookingDtoWithId) {
        Booking booking = delegate.toBooking(bookingDtoWithId);
        UserDto booker = userService.getUser(bookingDtoWithId.getUserId());
        ItemDto item = itemService.getItem(bookingDtoWithId.getUserId(), bookingDtoWithId.getItemId());
        booking.setBooker(userMapper.toUser(booker));
        booking.setItem(itemMapper.toItem(item));
        return booking;
    }
}