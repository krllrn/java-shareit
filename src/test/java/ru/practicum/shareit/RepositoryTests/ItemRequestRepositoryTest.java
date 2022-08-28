package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRequestRepositoryTest {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    User user = new User("test@test.com", "Test Testov");
    User user2 = new User("test2@test.com", "Test2 Testov");
    User user3 = new User("test3@test.com", "Test3 Testov");
    ItemRequest itemRequest1 = new ItemRequest("Description1", LocalDateTime.now(), user);
    ItemRequest itemRequest2 = new ItemRequest("Description2", LocalDateTime.now(), user2);
    ItemRequest itemRequest3 = new ItemRequest("Description2", LocalDateTime.now(), user3);

    @Autowired
    public ItemRequestRepositoryTest(ItemRequestRepository itemRequestRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);
    }

    @Test
    public void testFindByRequestOwnerIdContaining() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestOwnerIdContaining(user2.getId());

        Assertions.assertEquals(itemRequest2.getDescription(), itemRequestList.get(0).getDescription());
        Assertions.assertEquals(itemRequest2.getReqOwnerId().getId(), itemRequestList.get(0).getReqOwnerId().getId());
    }

    @Test
    public void testFindByIdIs() {
        ItemRequest foundItemRequest = itemRequestRepository.findByIdIs(itemRequest2.getId());

        Assertions.assertEquals(itemRequest2.getDescription(), foundItemRequest.getDescription());
        Assertions.assertEquals(itemRequest2.getReqOwnerId().getId(), foundItemRequest.getReqOwnerId().getId());
    }
}
