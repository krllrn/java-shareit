package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByIdIs() {
        User user = new User("test@test.com", "Test Testov");
        User user2 = new User("test2@test.com", "Test2 Testov");
        User user3 = new User("test3@test.com", "Test3 Testov");

        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);

        User foundUser = userRepository.findByIdIs(user2.getId());

        Assertions.assertEquals(foundUser.getName(), user2.getName());
        Assertions.assertEquals(foundUser.getEmail(), user2.getEmail());
    }
}
