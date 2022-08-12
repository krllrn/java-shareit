/*
package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShareItTests {
	private final UserStorage userStorage;
	private final ItemStorage itemStorage;
	private final Mapper mapper;

	@Autowired
	ShareItTests(UserStorage userStorage, ItemStorage itemStorage, Mapper mapper) {
		this.userStorage = userStorage;
		this.itemStorage = itemStorage;
		this.mapper = mapper;
	}

	private final User user1 = new User("test@test.ru", "Test1");
	private final User user2 = new User("test2@test2.ru", "Test2");
	private final ItemDto itemDto1 = new ItemDto("Test item1", "Loooooooong description1", true);
	private final ItemDto itemDto2 = new ItemDto("Test item2", "Loooooooong description2", false);

	@BeforeEach
	public void clear() {
		userStorage.deleteAll();
		itemStorage.deleteAll();
	}

	@Test
	public void testCreateUser() {
		Optional<User> userOptional = Optional.ofNullable(userStorage.create(user1));

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
		userStorage.create(user1);
		Optional<User> userOptionalT = Optional.ofNullable(userStorage.getUserById(user1.getId()));

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
		userStorage.create(user1);
		userStorage.create(user2);

		assertEquals(2, userStorage.getUsers().size());
	}

	@Test
	public void testUpdateUser() {
		userStorage.create(user1);
		userStorage.update(user1.getId(), user2);
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(user1.getId()));

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
		userStorage.create(user1);
		userStorage.create(user2);
		userStorage.delete(user1.getId());

		assertEquals(1, userStorage.getUsers().size());
	}

	// ~~~~~~~~~~~~~~~ ITEMS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Test
	public void testGetItems() {
		userStorage.create(user1);
		itemStorage.addItem(user1.getId(), mapper.itemToEntity(user1.getId(), itemDto1));
		itemStorage.addItem(user1.getId(), mapper.itemToEntity(user1.getId(), itemDto2));

		assertEquals(2, itemStorage.getItems(user1.getId()).size());
	}

	@Test
	public void testGetItemById() {
		userStorage.create(user1);
		Item item1 = itemStorage.addItem(user1.getId(), mapper.itemToEntity(user1.getId(), itemDto1));
		Item item2 = itemStorage.addItem(user1.getId(), mapper.itemToEntity(user1.getId(), itemDto2));

		Optional<Item> itemOptional = Optional.ofNullable(itemStorage.getItemById(item2.getId()));

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
		userStorage.create(user1);
		Item toEdit = itemStorage.addItem(1, mapper.itemToEntity(user1.getId(), itemDto1));

		Optional<Item> itemOptional = Optional.ofNullable(itemStorage.edit(toEdit.getId(), user1.getId(), itemDto2));

		assertThat(itemOptional)
				.isPresent()
				.hasValueSatisfying(item ->
						assertThat(item).hasFieldOrPropertyWithValue("name", "Test item2")
				)
				.hasValueSatisfying(item ->
						assertThat(item).hasFieldOrPropertyWithValue("description", "Loooooooong description2")
				);
	}
}
*/