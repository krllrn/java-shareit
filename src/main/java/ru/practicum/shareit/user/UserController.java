package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("Получен запрос /GET для формирования списка всех пользователей.");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.debug("Получен запрос /GET для формирования данных пользователя с id: {}.", id);
        return userService.getById(id);
    }

    @PostMapping
    public UserDto create(@PathVariable (required = false) Long id,
                          @Valid @RequestBody UserDto userDto) {
        log.debug("Получен запрос /POST для создания пользователя с id: {}.", id);
        log.debug("Данные пользователя: {}.", userDto);
        return userService.create(id, userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateValues(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.debug("Получен запрос /PATCH для обновления данных пользователя с id: {}.", id);
        log.debug("Данные для обновления: {}.", userDto);
        return userService.updateValues(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.debug("Получен запрос /DELETE для удаления пользователя с id: {}.", id);
        userService.delete(id);
    }
}
