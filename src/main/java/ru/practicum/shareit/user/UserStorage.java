package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);
    List<User> getUsers();
    User getUserById(long id);
    User update(long id, User user);
    void delete(long id);
    void deleteAll();
}
