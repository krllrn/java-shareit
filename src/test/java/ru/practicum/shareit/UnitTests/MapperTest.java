package ru.practicum.shareit.UnitTests;

import org.assertj.core.internal.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureTestDatabase
public class MapperTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    User user = new User(1L, "test@test.com", "Test Testov");
    User user2 = new User(2L, "test2@test.com", "Test2 Testov");
    User user3 = new User(3L, "test3@test.com", "Test3 Testov");
    Item item1 = new Item(1L, user, "Name1", "Description1", true, null, 1L);
    ItemDto itemDto = new ItemDto("Name1", "Description1", true);
    ItemShort itemShort = new ItemShort(1L, "Name1");
    Item item2 = new Item(2L, user2, "Name2", "Description2", true, null, 1L);
    Item item3 = new Item(3L, user3, "Name3", "Description3", true, null, 1L);
    Booking booking1 = new Booking(1L, item2, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), user,
            BookingState.WAITING, null, item2.getOwner().getId(), null);
    Booking booking2 = new Booking(2L, item3, LocalDateTime.now(), LocalDateTime.now().plusDays(2), user2,
            BookingState.APPROVED, null, item3.getOwner().getId(), null);
    Booking booking3 = new Booking(3L, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), user3,
            BookingState.REJECTED, null, item1.getOwner().getId(), null);
    Booking booking4 = new Booking(4L, item1, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), user3,
            BookingState.APPROVED, null, item1.getOwner().getId(), null);
    BookingDto bookingDto = new BookingDto(new ItemShort(item1.getId(), item1.getName()), LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1));
    BookingShort bookingShort = new BookingShort(4L, 3L);
    Comment comment = new Comment("Test comment");

    @Test
    void testItemToDto() {
        Mapper mapper = new Mapper(modelMapper, userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        Mockito.when(modelMapper.map(any(), any()))
                        .thenReturn(itemDto);
        Mockito.when(bookingRepository.findByItemIdAndEndDate(any(), any()))
                .thenReturn(booking4);
        Mockito.when(modelMapper.map(any(Booking.class), any()))
                .thenReturn(bookingShort);
        Mockito.when(commentRepository.findAllByItemId(any()))
                        .thenReturn(commentList);
        itemDto.setComments(commentList);
        itemDto.setLastBooking(bookingShort);
        Assertions.assertEquals(itemDto, mapper.itemToDto(item1));
    }

    @Test
    void testItemToDtoWoBookings() {
        Mapper mapper = new Mapper(modelMapper, userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        Mockito.when(modelMapper.map(any(), any()))
                .thenReturn(itemDto);
        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(commentList);

        itemDto.setComments(commentList);
        Assertions.assertEquals(itemDto, mapper.itemToDtoWoBookings(item1));
    }

    @Test
    void testItemToEntityItemIdNull() {
        Mapper mapper = new Mapper(modelMapper, userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        Mockito.when(modelMapper.map(any(ItemDto.class), any()))
                .thenReturn(item1);
        Mockito.when(userRepository.findByIdIs(any()))
                .thenReturn(user);

        Assertions.assertEquals(item1, mapper.itemToEntity(user.getId(), itemDto, null));
    }

    @Test
    void testItemToEntityItemIdNotNull() {
        Mapper mapper = new Mapper(modelMapper, userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        Mockito.when(itemRepository.findByIdIs(any()))
                        .thenReturn(item1);
        Mockito.when(modelMapper.map(any(ItemDto.class), any()))
                .thenReturn(item1);
        Mockito.when(userRepository.findByIdIs(any()))
                .thenReturn(user);

        Assertions.assertEquals(item1, mapper.itemToEntity(user.getId(), itemDto, item1.getId()));
    }

    @Test
    void testItemToEntityItemIdNotNullException() {
        Mapper mapper = new Mapper(modelMapper, userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        Mockito.when(itemRepository.findByIdIs(any()))
                .thenReturn(item1);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            mapper.itemToEntity(user2.getId(), itemDto, item1.getId());
        });
        Assertions.assertTrue(exception.getMessage().contains("The server understood the request but " +
                "refuses to authorize it"));
    }

    @Test
    void testItemToShort() {
        Mapper mapper = new Mapper(modelMapper, userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        Mockito.when(modelMapper.map(any(Item.class), any()))
                .thenReturn(itemShort);

        Assertions.assertEquals(itemShort, mapper.itemToShort(item1));
    }
}
