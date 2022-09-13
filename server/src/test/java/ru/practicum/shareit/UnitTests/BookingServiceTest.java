package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureTestDatabase
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private Mapper mapper;

    @Mock
    private UserRepository userRepository;

    User user = new User(1L, "test@test.com", "Test Testov");
    User user2 = new User(2L, "test2@test.com", "Test2 Testov");
    User user3 = new User(3L, "test3@test.com", "Test3 Testov");
    Item item1 = new Item(1L, user, "Name1", "Description1", true, null, 1L);
    Item item2 = new Item(2L, user2, "Name2", "Description2", true, null, 1L);
    Item item3 = new Item(3L, user3, "Name3", "Description3", true, null, 1L);
    Booking booking1 = new Booking(1L, item2, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), user,
            BookingState.WAITING, null, item2.getOwner().getId(), null);
    Booking booking1App = new Booking(1L, item2, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), user,
            BookingState.APPROVED, null, item2.getOwner().getId(), null);
    Booking booking2 = new Booking(2L, item3, LocalDateTime.now(), LocalDateTime.now().plusDays(2), user2,
            BookingState.APPROVED, null, item3.getOwner().getId(), null);
    Booking booking3 = new Booking(3L, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), user3,
            BookingState.REJECTED, null, item1.getOwner().getId(), null);
    Booking booking4 = new Booking(4L, item1, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), user3,
            BookingState.APPROVED, null, item1.getOwner().getId(), null);
    BookingDto bookingDto = new BookingDto(new ItemShort(item1.getId(), item1.getName()), LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1));

    @Test
    public void testAddBookingItemNotFound() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Mockito.when(itemRepository.findByIdIs(any()))
                .thenReturn(null);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });
        Assertions.assertTrue(exception.getMessage().contains("Item not found!"));
    }

    @Test
    public void testAddBooking() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Mockito.when(mapper.bookingDtoToEntity(any(), any()))
                .thenReturn(booking1);
        Mockito.when(itemRepository.findByIdIs(any()))
                        .thenReturn(item1);
        Mockito.when(userRepository.findByIdIs(any()))
                .thenReturn(user);
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking1);
        Mockito.when(mapper.bookingToDto(any()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(bookingDto, bookingService.addBooking(user.getId(), bookingDto));
    }

    @Test
    public void testUpdateBookingTrue() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        booking1.setItemOwnerId(user.getId());
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(booking1);
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking1);
        BookingDto bookingDtoApp = mapper.bookingToDto(booking1App);
        Mockito.when(mapper.bookingToDto(any()))
                .thenReturn(bookingDtoApp);

        Assertions.assertEquals(bookingDtoApp, bookingService.updateBooking("true", booking1.getId(), user.getId()));
    }

    @Test
    public void testUpdateBookingFalse() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        booking1.setItemOwnerId(user.getId());
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(booking1);
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking1);
        BookingDto bookingDtoApp = mapper.bookingToDto(booking1App);
        Mockito.when(mapper.bookingToDto(any()))
                .thenReturn(bookingDtoApp);

        Assertions.assertEquals(bookingDtoApp, bookingService.updateBooking("false", booking1.getId(), user.getId()));
    }

    @Test
    public void testUpdateBookingStatusApprovedAndTrue() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        booking1.setItemOwnerId(user.getId());
        booking1.setStatus(BookingState.APPROVED);
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(booking1);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.updateBooking("true", booking1.getId(), user.getId());
        });
        Assertions.assertTrue(exception.getMessage().contains("Booking already approved."));
    }

    @Test
    public void testUpdateBookingAccess() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(booking1);
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking1);
        BookingDto bookingDtoApp = mapper.bookingToDto(booking1App);
        Mockito.when(mapper.bookingToDto(any()))
                .thenReturn(bookingDtoApp);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.updateBooking("true", booking1.getId(), user.getId());
        });
        Assertions.assertTrue(exception.getMessage().contains("Only owner have access."));
    }

    @Test
    public void testGetInfoAboutBookingNotFound() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(null);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.getInfoAboutBooking(user.getId(), booking1.getId());
        });
        Assertions.assertTrue(exception.getMessage().contains("Booking not found!"));
    }

    @Test
    public void testGetInfoAboutBooking() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        booking1.setItemOwnerId(user.getId());
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(booking1);
        BookingDto bookingDtoApp = mapper.bookingToDto(booking1);
        Mockito.when(mapper.bookingToDto(any()))
                .thenReturn(bookingDtoApp);

        Assertions.assertEquals(bookingDtoApp, bookingService.getInfoAboutBooking(user.getId(), booking1.getId()));
    }

    @Test
    public void testGetInfoAboutBookingOwnerAndBookerEquals() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        booking1.setItemOwnerId(user2.getId());
        booking1.setBooker(user2);
        Mockito.when(bookingRepository.findByIdIs(any()))
                .thenReturn(booking1);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.getInfoAboutBooking(user.getId(), booking1.getId());
        });
        Assertions.assertTrue(exception.getMessage().contains("Only owner or booker have access"));
    }

    @Test
    public void testGetUserBookingsWrongFromSize() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.getUserBookings(user.getId(), "ALL", -1, 0);
        });
        Assertions.assertTrue(exception.getMessage().contains("Incorrect parameters FROM or SIZE!"));
    }

    @Test
    public void testGetUserBookingsAll() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking4);
        bookingsList.add(booking3);
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                        .map(mapper::bookingToDto)
                                .collect(Collectors.toList());
        Mockito.when(bookingRepository.findAllByBookerId(any(), any()))
                        .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getUserBookings(user.getId(), "ALL", 0, 20).size());
    }

    @Test
    public void testGetUserBookingsCurrent() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking4);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        Mockito.when(bookingRepository.findAllByBookerIdAndStartAfterAndEndBefore(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getUserBookings(user.getId(), "CURRENT", 0, 20).size());
    }

    @Test
    public void testGetUserBookingsPast() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        Mockito.when(bookingRepository.findAllByBookerIdInPast(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getUserBookings(user.getId(), "PAST", 0, 20).size());
    }

    @Test
    public void testGetUserBookingsFuture() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking3);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        Mockito.when(bookingRepository.findAllByBookerIdInFuture(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getUserBookings(user.getId(), "FUTURE", 0, 20).size());
    }

    @Test
    public void testGetUserBookingsWaiting() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking1);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getUserBookings(user.getId(), "WAITING", 0, 20).size());
    }

    @Test
    public void testGetUserBookingsRejected() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking3);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getUserBookings(user.getId(), "REJECTED", 0, 20).size());
    }

    @Test
    public void testGetUserBookingsDefault() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Exception exception = assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getUserBookings(user.getId(), "TEST", 0, 20);
        });
        Assertions.assertTrue(exception.getMessage().contains("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    public void testGetBookingsForUserItemsWrongFromSize() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.getBookingsForUserItems("ALL", user.getId(),  -1, 0);
        });
        Assertions.assertTrue(exception.getMessage().contains("Incorrect parameters FROM or SIZE!"));
    }

    @Test
    public void testGetBookingsForUserItemsAll() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking4);
        bookingsList.add(booking3);
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item3);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Mockito.when(bookingRepository.findByItemOwnerId(any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getBookingsForUserItems("ALL", user.getId(),
                0, 20).size());
    }

    @Test
    public void testGetBookingsForUserItemsAllNoItems() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Item> itemList = new ArrayList<>();
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.getBookingsForUserItems("ALL", user.getId(),  0, 20);
        });
        Assertions.assertTrue(exception.getMessage().contains("User don't have enough items!"));
    }

    @Test
    public void testGetBookingsForUserItemsCurrent() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndBefore(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getBookingsForUserItems("CURRENT", user.getId(),
                0, 20).size());
    }

    @Test
    public void testGetBookingsForUserItemsPast() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Mockito.when(bookingRepository.findAllByItemOwnerIdInPast(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getBookingsForUserItems("PAST", user.getId(),
                0, 20).size());
    }

    @Test
    public void testGetBookingsForUserItemsFuture() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Mockito.when(bookingRepository.findAllByItemOwnerIdInFuture(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getBookingsForUserItems("FUTURE", user.getId(),
                0, 20).size());
    }

    @Test
    public void testGetBookingsForUserItemsWaiting() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getBookingsForUserItems("WAITING", user.getId(),
                0, 20).size());
    }

    @Test
    public void testGetBookingsForUserItemsRejected() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking2);
        List<BookingDto> bookingDtoList = bookingsList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(any(), any(), any()))
                .thenReturn(bookingsList);

        Assertions.assertEquals(bookingDtoList.size(), bookingService.getBookingsForUserItems("REJECTED", user.getId(),
                0, 20).size());
    }

    @Test
    public void testGetBookingsForUserItemsDefault() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        Mockito.when(itemRepository.findByUserIdContaining(any()))
                .thenReturn(itemList);
        Exception exception = assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getBookingsForUserItems("TEST", user.getId(), 0, 20);
        });
        Assertions.assertTrue(exception.getMessage().contains("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    public void testCheckCorrect() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, userService, mapper);
        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndStartDateCorrectOrStatus(any(), any(), any(), any()))
                .thenReturn(null);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.checkCorrect(1L, 2L, LocalDateTime.now(), BookingState.REJECTED);
        });
        Assertions.assertTrue(exception.getMessage().contains("This user didn't take this item."));
    }
}
