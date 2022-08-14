package ru.practicum.shareit.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserId;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Mapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public Mapper(ModelMapper modelMapper, UserRepository userRepository, ItemRepository itemRepository,
                  BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    // ---------------- ITEM ------------------------------------------------------------

    public ItemDto itemToDto(Item item) {
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        if (bookingRepository.findByItemIdAndEndDate(item.getId(), LocalDateTime.now()) != null) {
            itemDto.setLastBooking(modelMapper.map(bookingRepository.findByItemIdAndEndDate(item.getId(), LocalDateTime.now()),
                    BookingShort.class));
        }
        if (bookingRepository.findByItemIdAndStartDate(item.getId(), LocalDateTime.now()) != null) {
            itemDto.setNextBooking(modelMapper.map(bookingRepository.findByItemIdAndStartDate(item.getId(), LocalDateTime.now()),
                    BookingShort.class));
        }
        List<Comment> commentList = commentRepository.findAllByItemId(item.getId());
        itemDto.setComments(commentList);
        return itemDto;
    }

    public ItemDto itemToDtoWoBookings(Item item) {
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        List<Comment> commentList = commentRepository.findAllByItemId(item.getId());
        itemDto.setComments(commentList);
        return itemDto;
    }

    public Item itemToEntity(long userId, ItemDto itemDto, Long itemId) {
        Item item;
        if (itemId == null) {
            item = modelMapper.map(itemDto, Item.class);
            item.setOwner(userRepository.getReferenceById(userId));
        } else {
            item = itemRepository.getReferenceById(itemId);
            if (item.getOwner().getId() != userId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The server understood the request but " +
                        "refuses to authorize it");
            } else {
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
            }
        }
        return item;
    }

    // ---------------- USER ------------------------------------------------------------

    public UserDto userToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User userToEntity(Long id, UserDto userDto) {
        if (id == null) {
            return modelMapper.map(userDto, User.class);
        } else {
            User userToUpdate = userRepository.getReferenceById(id);
            if (userDto.getEmail() != null) {
                userToUpdate.setEmail(userDto.getEmail());
            }
            if (userDto.getName() != null) {
                userToUpdate.setName(userDto.getName());
            }
            return userToUpdate;
        }
    }

    // ---------------- BOOKING ------------------------------------------------------------

    public BookingDto bookingToDto(Booking booking) {
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        bookingDto.setBooker(modelMapper.map(booking.getBooker(), UserId.class));
        bookingDto.setItem(modelMapper.map(booking.getItem(), ItemShort.class));
        return bookingDto;
    }

    public Booking bookingDtoToEntity(BookingDto bookingDto, Long userId) {
        Booking booking = modelMapper.map(bookingDto, Booking.class);
        Item item = itemRepository.findAllByIdContaining((bookingDto.getItem().getId()));
        if (userId == item.getOwner().getId()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OWNER and BOOKER equals");
        }
        if (userRepository.findByIdContaining(userId) != null) {
            booking.setBooker(userRepository.getReferenceById(userId));
        }
        if (item != null) {
            booking.setItem(item);
            booking.setItemOwnerId(item.getOwner().getId());
        }
        if (!itemRepository.findAllByIdContaining(booking.getItem().getId()).getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item isn't available!");
        }
        return booking;
    }
}
