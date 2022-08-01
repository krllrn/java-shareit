package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto create(UserDto userDto);

    List<UserDto> getUsers();

    UserDto getUserById(long id);

    UserDto update(long id, UserDto userDto);

    void delete(long id);

    void deleteAll();
}
