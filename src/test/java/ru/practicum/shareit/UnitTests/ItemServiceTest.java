package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureTestDatabase
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private Mapper mapper;

    private final User user = new User(1L, "test1", "test1@email.com");
    private final Item item1 = new Item(1L, user, "Name1", "Description1", true, null, 1L);
    private final ItemDto itemDto = new ItemDto("Name1", "Description1", true);
    private final Comment comment = new Comment("Test comment");
    Booking booking1 = new Booking(1L, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), user,
            BookingState.WAITING, null, item1.getOwner().getId(), null);

    @Test
    public void testSearchWithSizeWrong() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.search("Text", -1, 0);
        });
        Assertions.assertTrue(exception.getMessage().contains("Incorrect parameters FROM or SIZE!"));
    }

    @Test
    public void testAddComment() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(itemRepository.findByIdIs(any()))
                .thenReturn(item1);
        Mockito.when(userRepository.findByIdIs(any()))
                        .thenReturn(user);
        item1.setComment(comment);

        Assertions.assertEquals(item1, itemService.addComment(item1.getId(), user.getId(), comment));
    }

    @Test
    public void testGetItemsWrongUserId() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.getItems(null, 0, 20);
        });
        Assertions.assertTrue(exception.getMessage().contains("No USER_ID."));
    }

    @Test
    public void testGetItemsWrongFromSize() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.getItems(1L, -1, 0);
        });
        Assertions.assertTrue(exception.getMessage().contains("Incorrect parameters FROM or SIZE!"));
    }

    @Test
    public void testGetItems() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);

        Assertions.assertEquals(itemList.size(), itemService.getItems(user.getId(), null, null).size());
    }

    @Test
    public void testGetItemByIdWrongItem() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(itemRepository.findByIdIs(any()))
                .thenReturn(null);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.getItemById(1L, 1L);
        });
        Assertions.assertTrue(exception.getMessage().contains("Item not found."));
    }

    @Test
    public void testGetItemById() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(itemRepository.findByIdIs(any()))
                .thenReturn(item1);
        Mockito.when(mapper.itemToDto(any()))
                .thenReturn(itemDto);

        Assertions.assertEquals(itemDto, itemService.getItemById(1L, 1L));
    }

    @Test
    public void testAddItemByIdNoUserId() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.addItem(1L, null, itemDto);
        });
        Assertions.assertTrue(exception.getMessage().contains("No USER_ID. Only owner have access"));
    }

    @Test
    public void testAddItemByIdUserNotFound() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(userRepository.findByIdIs(any()))
                .thenReturn(null);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.addItem(1L, 1L, itemDto);
        });
        Assertions.assertTrue(exception.getMessage().contains("User not found!"));
    }

    @Test
    public void testAddItem() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(mapper.itemToEntity(user.getId(), itemDto, item1.getId()))
                .thenReturn(item1);
        Mockito.when(userRepository.findByIdIs(user.getId()))
                .thenReturn(user);
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item1);
        Mockito.when(mapper.itemToDto(any()))
                .thenReturn(itemDto);

        Assertions.assertEquals(itemDto, itemService.addItem(item1.getId(), user.getId(), itemDto));
    }

    @Test
    public void testEditItemByIdNoUserId() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.edit(1L, null, itemDto);
        });
        Assertions.assertTrue(exception.getMessage().contains("No USER_ID. Only owner have access"));
    }

    @Test
    public void testEditItemByIdUserNotFound() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(userRepository.findByIdIs(any()))
                .thenReturn(null);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemService.edit(1L, 1L, itemDto);
        });
        Assertions.assertTrue(exception.getMessage().contains("User not found!"));
    }

    @Test
    public void testEditItem() {
        ItemServiceImpl itemService = new ItemServiceImpl(bookingService, itemRepository, commentRepository, userRepository, mapper);
        Mockito.when(mapper.itemToEntity(user.getId(), itemDto, item1.getId()))
                .thenReturn(item1);
        Mockito.when(userRepository.findByIdIs(user.getId()))
                .thenReturn(user);
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item1);
        Mockito.when(mapper.itemToDto(any()))
                .thenReturn(itemDto);

        Assertions.assertEquals(itemDto, itemService.edit(item1.getId(), user.getId(), itemDto));
    }
}
