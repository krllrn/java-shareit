package ru.practicum.shareit.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class Mapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public Mapper(ModelMapper modelMapper, UserRepository userRepository, ItemRepository itemRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public ItemDto itemToDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item itemToEntity(long userId, ItemDto itemDto, Long itemId) {
        Item item;
        if (itemId == null) {
            item = modelMapper.map(itemDto, Item.class);
            item.setOwner(userRepository.getReferenceById(userId));
        } else {
            userRepository.getReferenceById(userId);
            item = itemRepository.getReferenceById(itemId);
            if (item.getOwner().getId() != userId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The server understood the request but " +
                        "refuses to authorize it");
            } else {
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
            }
        }
        return item;
    }

    public UserDto userToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User userToEntity(Long id, UserDto userDto) {
        if (id == null) {
            return modelMapper.map(userDto, User.class);
        } else {
            User userToUpdate = userRepository.getReferenceById(id);
            if (userDto.getEmail() != null) {
                userToUpdate.setEmail(userDto.getEmail());
            }
            if (userDto.getName() != null) {
                userToUpdate.setName(userDto.getName());
            }
            return userToUpdate;
        }
    }
}
