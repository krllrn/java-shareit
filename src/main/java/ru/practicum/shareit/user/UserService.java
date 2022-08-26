package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto create(Long id, UserDto userDto);

    UserDto updateValues(Long id, UserDto userDto);

    void delete(Long id);

    void checkUser(Long id);
}
