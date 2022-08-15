package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShareItTests {
	private final UserRepository userStorage;
	private final ItemRepository itemStorage;
	private final BookingRepository bookingRepository;
	private final ItemService itemService;
	private final Mapper mapper;

	@Autowired
	ShareItTests(UserRepository userStorage, ItemRepository itemStorage, BookingRepository bookingRepository, ItemService itemService, Mapper mapper) {
		this.userStorage = userStorage;
		this.itemStorage = itemStorage;
		this.bookingRepository = bookingRepository;
		this.itemService = itemService;
		this.mapper = mapper;
	}

	private final User user1 = new User("test@test.ru", "Test1");
	private final User user2 = new User("test2@test2.ru", "Test2");
	private final ItemDto itemDto1 = new ItemDto("Test item1", "Loooooooong description1", true);
	private final ItemDto itemDto2 = new ItemDto("Test item2", "Loooooooong description2", true);

	@BeforeEach
	public void clear() {
		userStorage.deleteAll();
		itemStorage.deleteAll();
	}

	@Test
	public void testCreateUser() {
		Optional<User> userOptional = Optional.of(userStorage.save(user1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("email", "test@test.ru")
				)
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "Test1")
				);
	}

	@Test
	public void testGetUserById() {
		userStorage.save(user1);
		Optional<User> userOptionalT = Optional.ofNullable(userStorage.findByIdIs(user1.getId()));

		assertThat(userOptionalT)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("email", "test@test.ru")
				)
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "Test1")
				);
	}

	@Test
	public void testReturnAllUsers() {
		userStorage.save(user1);
		userStorage.save(user2);

		assertEquals(2, userStorage.findAll().size());
	}

	@Test
	public void testUpdateUser() {
		userStorage.save(user1);
		user2.setId(user1.getId());
		userStorage.save(user2);
		Optional<User> userOptional = Optional.ofNullable(userStorage.findByIdIs(user1.getId()));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("email", "test2@test2.ru")
				)
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "Test2")
				);
	}

	@Test
	public void testDeleteUserById() {
		userStorage.save(user1);
		userStorage.save(user2);
		userStorage.delete(user1);

		assertEquals(1, userStorage.findAll().size());
	}

	// ~~~~~~~~~~~~~~~ ITEMS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Test
	public void testGetItems() {
		userStorage.save(user1);
		itemStorage.save(mapper.itemToEntity(user1.getId(), itemDto1, null));
		itemStorage.save(mapper.itemToEntity(user1.getId(), itemDto2, null));

		assertEquals(2, itemStorage.findAll().size());
	}

	@Test
	public void testGetItemById() {
		userStorage.save(user1);
		Item item1 = itemStorage.save(mapper.itemToEntity(user1.getId(), itemDto1, null));
		Item item2 = itemStorage.save(mapper.itemToEntity(user1.getId(), itemDto2, null));

		Optional<Item> itemOptional = Optional.ofNullable(itemStorage.findByIdIs(item2.getId()));

		assertThat(itemOptional)
				.isPresent()
				.hasValueSatisfying(item ->
						assertThat(item).hasFieldOrPropertyWithValue("name", "Test item2")
				)
				.hasValueSatisfying(item ->
						assertThat(item).hasFieldOrPropertyWithValue("description", "Loooooooong description2")
				);
	}

	@Test
	public void testEditItem() {
		User user = userStorage.save(user1);
		Item item1 = itemStorage.save(mapper.itemToEntity(userStorage.findByIdIs(user.getId()).getId(), itemDto1, null));

		Optional<Item> itemOptional = Optional.of(itemStorage.save(mapper.itemToEntity(user.getId(),
				itemDto2, item1.getId())));

		assertThat(itemOptional)
				.isPresent()
				.hasValueSatisfying(item ->
						assertThat(item).hasFieldOrPropertyWithValue("name", "Test item2")
				)
				.hasValueSatisfying(item ->
						assertThat(item).hasFieldOrPropertyWithValue("description", "Loooooooong description2")
				);
	}

	//-------------BOOKING---------------------------------
	@Test
	public void testCreateBooking() {
		User user = userStorage.save(user1);
		User booker = userStorage.save(user2);
		Item item1 = itemStorage.save(mapper.itemToEntity(user.getId(), itemDto1, null));
		Item item2 = itemStorage.save(mapper.itemToEntity(user.getId(), itemDto2, null));

		BookingDto bookingDto = new BookingDto(mapper.itemToShort(item2), LocalDateTime.now(),
				LocalDateTime.now().plusDays(1));

		bookingDto.setStatus(BookingState.WAITING);
		Optional<Booking> bookingOptional =
				Optional.of(bookingRepository.save(mapper.bookingDtoToEntity(bookingDto, booker.getId())));

		assertThat(bookingOptional)
				.isPresent()
				.hasValueSatisfying(booking ->
						assertThat(booking).hasFieldOrPropertyWithValue("itemOwnerId", user1.getId())
				)
				.hasValueSatisfying(booking ->
						assertThat(booking).hasFieldOrPropertyWithValue("status", BookingState.WAITING)
				);
	}

	@Test
	public void testGetBookings() {
		User user = userStorage.save(user1);
		User booker = userStorage.save(user2);
		Item item1 = itemStorage.save(mapper.itemToEntity(user.getId(), itemDto1, null));
		Item item2 = itemStorage.save(mapper.itemToEntity(user.getId(), itemDto2, null));

		BookingDto bookingDto1 = new BookingDto(mapper.itemToShort(item1), LocalDateTime.now(),
				LocalDateTime.now().plusDays(1));
		BookingDto bookingDto2 = new BookingDto(mapper.itemToShort(item2), LocalDateTime.now(),
				LocalDateTime.now().plusDays(2));

		bookingDto1.setStatus(BookingState.WAITING);
		bookingDto2.setStatus(BookingState.WAITING);
		bookingRepository.save(mapper.bookingDtoToEntity(bookingDto1, booker.getId()));
		bookingRepository.save(mapper.bookingDtoToEntity(bookingDto2, booker.getId()));

		assertEquals(2, bookingRepository.findAll().size());
	}

	//----------------------------COMMENTS----------------------------------------
	@Test
	public void testCreateComment() {
		User user = userStorage.save(user1);
		User booker = userStorage.save(user2);
		Item item1 = itemStorage.save(mapper.itemToEntity(user.getId(), itemDto1, null));
		Item item2 = itemStorage.save(mapper.itemToEntity(user.getId(), itemDto2, null));

		BookingDto bookingDto1 = new BookingDto(mapper.itemToShort(item1), LocalDateTime.now(),
				LocalDateTime.now().plusDays(1));
		BookingDto bookingDto2 = new BookingDto(mapper.itemToShort(item2), LocalDateTime.now().minusDays(5),
				LocalDateTime.now().minusDays(2));

		Comment commentNew = new Comment("Comment for test");

		bookingDto1.setStatus(BookingState.WAITING);
		bookingDto2.setStatus(BookingState.WAITING);
		bookingRepository.save(mapper.bookingDtoToEntity(bookingDto1, booker.getId()));
		bookingRepository.save(mapper.bookingDtoToEntity(bookingDto2, booker.getId()));

		Optional<Comment> testComment =
				Optional.of(itemService.addComment(item2.getId(), booker.getId(), commentNew).getComment());

		assertThat(testComment)
				.isPresent()
				.hasValueSatisfying(comment ->
						assertThat(comment).hasFieldOrPropertyWithValue("text", "Comment for test")
				)
				.hasValueSatisfying(booking ->
						assertThat(booking).hasFieldOrPropertyWithValue("authorName", "Test2")
				);
	}
}