package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    User user = new User("test@test.com", "Test Testov");
    User user2 = new User("test2@test.com", "Test2 Testov");
    User user3 = new User("test3@test.com", "Test3 Testov");
    ItemRequest itemRequest1 = new ItemRequest("Description1", LocalDateTime.now(), user);
    Item item1 = new Item(user, "Name1", "Description1", true, null, null);
    Item item2 = new Item(user2, "Name2", "Description2", false, null, null);
    Item item3 = new Item(user2, "Name3", "Description3", true, null, null);

    @Autowired
    public ItemRepositoryTest(ItemRepository itemRepository, UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRequestRepository.save(itemRequest1);
        item1.setRequestId(itemRequest1.getId());
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    public void testFindByUserIdContaining() {
        List<Item> foundedItems = itemRepository.findByUserIdContaining(user2.getId());

        assertEquals(2, foundedItems.size());
        assertEquals(item2.getName(), foundedItems.get(0).getName());
        assertEquals(item2.getDescription(), foundedItems.get(0).getDescription());
    }

    @Test
    public void testFindByRequestIdContaining() {
        List<Item> foundedItems = itemRepository.findByRequestIdContaining(itemRequest1.getId());

        assertEquals(1, foundedItems.size());
        assertEquals(item1.getName(), foundedItems.get(0).getName());
        assertEquals(item1.getDescription(), foundedItems.get(0).getDescription());
    }

    @Test
    public void testFindByIdIs() {
        Item foundedItem = itemRepository.findByIdIs(item3.getId());

        assertEquals(item3.getName(), foundedItem.getName());
        assertEquals(item3.getDescription(), foundedItem.getDescription());
    }
}
