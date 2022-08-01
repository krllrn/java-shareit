package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserDto> getAll() {
        return userStorage.getUsers().stream()
                .map(user -> mapper.userToDto(user))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return mapper.userToDto(userStorage.getUserById(id));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return mapper.userToDto(userStorage.create(mapper.userToEntity(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto updateValues(@PathVariable long id, @RequestBody UserDto userDto) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return mapper.userToDto(userStorage.update(id, mapper.userToEntity(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        userStorage.delete(id);
    }
}
