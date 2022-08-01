package ru.practicum.shareit.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class Mapper {

    private final ModelMapper modelMapper;
    private final UserStorage userStorage;

    @Autowired
    public Mapper(ModelMapper modelMapper, UserStorage userStorage) {
        this.modelMapper = modelMapper;
        this.userStorage = userStorage;
    }

    public ItemDto itemToDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item itemToEntity(long id, ItemDto itemDto) {
        Item item = modelMapper.map(itemDto, Item.class);
        item.setOwner(userStorage.getUserById(id));
        return item;
    }

    public UserDto userToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User userToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
