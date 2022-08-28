package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    private final User user = new User(1L, "test1", "test1@email.com");
    private final User user2 = new User(2L, "test2", "test2@email.com");
    private final UserDto userDto = new UserDto(1L, "test1", "test1@email.com");

    @Test
    public void testGetAll() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);
        Mockito.when(userRepository.findAll())
                        .thenReturn(userList);
        Assertions.assertEquals(userList.size(), userService.getAll().size());
    }

    @Test
    public void testGetById() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        Mockito.when(userRepository.findByIdIs(any()))
                .thenReturn(user);
        Mockito.when(mapper.userToDto(any(User.class)))
                        .thenReturn(userDto);
        UserDto foundedUser = userService.getById(userDto.getId());

        Assertions.assertEquals(userDto, foundedUser);
    }

    @Test
    public void testGetByWrongId() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getById(0L);
        });
        Assertions.assertTrue(exception.getMessage().contains("ID must be positive"));
    }

    @Test
    public void testCreate() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        Mockito.when(mapper.userToEntity(anyLong(), any(UserDto.class)))
                .thenReturn(user);
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);
        Mockito.when(mapper.userToDto(any(User.class)))
                .thenReturn(userDto);

        Assertions.assertEquals(userDto, userService.create(userDto.getId(), userDto));
    }

    @Test
    public void testUpdate() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        Mockito.when(mapper.userToEntity(anyLong(), any(UserDto.class)))
                .thenReturn(user);
        Mockito.when(userRepository.save(any()))
                .thenReturn(user);
        Mockito.when(mapper.userToDto(any()))
                .thenReturn(userDto);

        Assertions.assertEquals(userDto, userService.updateValues(userDto.getId(), userDto));
    }

    @Test
    public void testUpdateWrongId() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateValues(0L, userDto);
        });
        Assertions.assertTrue(exception.getMessage().contains("ID must be positive"));
    }

    @Test
    public void testDeleteWrongId() {
        UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userService.delete(0L);
        });
        Assertions.assertTrue(exception.getMessage().contains("ID must be positive"));
    }
}
