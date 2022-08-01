package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final Mapper mapper;

    @Autowired
    public UserController(UserStorage userStorage, Mapper mapper) {
        this.userStorage = userStorage;
        this.mapper = mapper;
    }

    @GetMapping
    public List<User> getAll() {
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return userStorage.getUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDto userDto) {
        return userStorage.create(mapper.userToEntity(userDto));
    }

    @PatchMapping("/{id}")
    public User updateValues(@PathVariable long id, @RequestBody UserDto userDto) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return userStorage.update(id, mapper.userToEntity(userDto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        userStorage.delete(id);
    }
}
