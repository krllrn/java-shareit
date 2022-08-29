package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class BookingRepositoryTest {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;
    User user = new User("test@test.com", "Test Testov");
    User user2 = new User("test2@test.com", "Test2 Testov");
    User user3 = new User("test3@test.com", "Test3 Testov");
    ItemRequest itemRequest1 = new ItemRequest("Description1", LocalDateTime.now(), user);
    Item item1 = new Item(user, "Name1", "Description1", true, null, null);
    Item item2 = new Item(user2, "Name2", "Description2", true, null, null);
    Item item3 = new Item(user3, "Name3", "Description3", true, null, null);
    Booking booking1 = new Booking(item2, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), user,
            BookingState.WAITING, null, item2.getOwner().getId(), null);
    Booking booking2 = new Booking(item3, LocalDateTime.now(), LocalDateTime.now().plusDays(2), user2, BookingState.APPROVED,
            null, item3.getOwner().getId(), null);
    Booking booking3 = new Booking(item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), user3,
            BookingState.REJECTED, null, item1.getOwner().getId(), null);
    Booking booking4 = new Booking(item1, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), user3,
            BookingState.APPROVED, null, item1.getOwner().getId(), null);

    @Autowired
    public BookingRepositoryTest(BookingRepository bookingRepository, ItemRepository itemRepository,
                                 UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRequestRepository.save(itemRequest1);
        item1.setRequestId(itemRequest1.getId());
        booking1.setItemOwnerId(item2.getOwner().getId());
        booking2.setItemOwnerId(item3.getOwner().getId());
        booking3.setItemOwnerId(item1.getOwner().getId());
        booking4.setItemOwnerId(item1.getOwner().getId());
        booking1.setBooker(user);
        booking2.setBooker(user2);
        booking3.setBooker(user3);
        booking4.setBooker(user3);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    @Test
    public void testFindByIdIs() {
        Booking foundedBooking = bookingRepository.findByIdIs(booking3.getId());

        assertEquals(booking3.getStatus(), foundedBooking.getStatus());
        assertEquals(booking3.getBooker().getId(), foundedBooking.getBooker().getId());
        assertEquals(booking3.getItem().getId(), foundedBooking.getItem().getId());
    }

    @Test
    public void testFindByItemIdAndEndDate() {
        Booking foundedBooking = bookingRepository.findByItemIdAndEndDate(item2.getId(), LocalDateTime.now().plusDays(5));

        assertEquals(booking1.getStatus(), foundedBooking.getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.getItem().getId());
    }

    @Test
    public void testFindByItemIdAndStartDate() {
        Booking foundedBooking = bookingRepository.findByItemIdAndStartDate(item3.getId(), LocalDateTime.now().minusDays(1));

        assertEquals(booking2.getStatus(), foundedBooking.getStatus());
        assertEquals(booking2.getBooker().getId(), foundedBooking.getBooker().getId());
        assertEquals(booking2.getItem().getId(), foundedBooking.getItem().getId());
    }

    @Test
    public void testFindByItemOwnerId() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findByItemOwnerId(item2.getOwner().getId(), page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByItemOwnerIdAndStatusApproved() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByItemOwnerIdAndStatus(item3.getOwner().getId(),
                BookingState.APPROVED, page);

        assertEquals(booking2.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking2.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking2.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByItemOwnerIdAndStatusWaiting() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByItemOwnerIdAndStatus(item2.getOwner().getId(),
                BookingState.WAITING, page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByItemOwnerIdAndStatusRejected() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByItemOwnerIdAndStatus(item1.getOwner().getId(),
                BookingState.REJECTED, page);

        assertEquals(booking3.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking3.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking3.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByItemOwnerIdAndStartAfterAndEndBefore() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndBefore(item2.getOwner().getId(),
                LocalDateTime.now(), page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByItemOwnerIdInPast() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByItemOwnerIdInPast(item2.getOwner().getId(),
                LocalDateTime.now().plusDays(7), page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerId() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerId(user.getId(), page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerIdAndStatusApproved() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerIdAndStatus(user2.getId(), BookingState.APPROVED,
                page);

        assertEquals(booking2.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking2.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking2.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerIdAndStatusWaiting() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerIdAndStatus(user.getId(), BookingState.WAITING,
                page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerIdAndStatusRejected() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerIdAndStatus(user3.getId(), BookingState.REJECTED,
                page);

        assertEquals(booking3.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking3.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking3.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerIdAndStartAfterAndEndBefore() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerIdAndStartAfterAndEndBefore(user.getId(),
                LocalDateTime.now(), page);

        assertEquals(booking1.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking1.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking1.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerIdInPast() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerIdInPast(user3.getId(),
                LocalDateTime.now().plusDays(17), page);

        assertEquals(booking3.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking3.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking3.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }

    @Test
    public void testFindAllByBookerIdInFuture() {
        Pageable page = PageRequest.of(0, 20);
        List<Booking> foundedBooking = bookingRepository.findAllByBookerIdInFuture(user3.getId(),
                LocalDateTime.now(), page);

        assertEquals(booking3.getStatus(), foundedBooking.get(0).getStatus());
        assertEquals(booking3.getBooker().getId(), foundedBooking.get(0).getBooker().getId());
        assertEquals(booking3.getItem().getId(), foundedBooking.get(0).getItem().getId());
    }
}
