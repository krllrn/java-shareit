package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserInMemoryStorage implements UserStorage {

    private final Mapper mapper;
    private Map<Long, User> users = new HashMap<>();
    private long userCount = 0;

    @Autowired
    public UserInMemoryStorage(@Lazy Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.userToEntity(userDto);
        for (User u : users.values()) {
            if (user.getEmail().equals(u.getEmail())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate email");
            }
        }
        user.setId(userCount + 1);
        users.put(user.getId(), user);
        userCount++;
        return mapper.userToDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return new ArrayList<>(users.values()).stream()
                .map(user -> mapper.userToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        if (users.containsKey(id)) {
            return mapper.userToDto(users.get(id));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = mapper.userToEntity(userDto);
        User updUser;
        if (users.containsKey(id)) {
            updUser = users.get(id);
            users.remove(id);
            if (user.getEmail() != null) {
                for (User u : users.values()) {
                    if (user.getEmail().equals(u.getEmail())) {
                        users.put(updUser.getId(), updUser);
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Email already exist");
                    }
                }
                updUser.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                updUser.setName(user.getName());
            }
            users.put(updUser.getId(), updUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return mapper.userToDto(updUser);
    }

    @Override
    public void delete(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
