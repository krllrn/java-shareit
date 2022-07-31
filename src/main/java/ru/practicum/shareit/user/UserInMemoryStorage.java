package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserInMemoryStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private long userCount = 0;

    @Override
    public User create(User user) {
        for (User u : users.values()) {
            if (user.getEmail().equals(u.getEmail())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate email");
            }
        }
        user.setId(userCount+1);
        users.put(user.getId(), user);
        userCount++;
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public User update(long id, User user) {
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
        return updUser;
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
