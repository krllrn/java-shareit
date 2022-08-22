package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Autowired
    ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository, UserRepository userRepository,
                    Mapper mapper) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> searchedItems = new ArrayList<>();
        for (Item i : itemRepository.findAll()) {
            if ((i.getName().toLowerCase().contains(text) || i.getDescription().toLowerCase().contains(text)) &&
                    i.getAvailable()) {
                searchedItems.add(i);
            }
        }
        return searchedItems;
    }

    @Override
    public Item addComment(Long itemId, Long userId, Comment comment) {
        Item item = itemRepository.findByIdIs(itemId);
        comment.setItemId(itemId);
        comment.setAuthorName(userRepository.findByIdIs(userId).getName());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        item.setComment(comment);
        return item;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID.");
        }
        return itemRepository.findByUserIdContaining(userId).stream()
                .map(mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        if (itemRepository.findByIdIs(itemId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found.");
        }
        if (userId != itemRepository.findByIdIs(itemId).getOwner().getId()) {
            return mapper.itemToDtoWoBookings(itemRepository.findByIdIs(itemId));
        }
        return mapper.itemToDto(itemRepository.findByIdIs(itemId));
    }

    @Override
    public ItemDto addItem(Long itemId, Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        if (userRepository.findByIdIs(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return mapper.itemToDto(itemRepository.save(mapper.itemToEntity(userId, itemDto, itemId)));
    }

    @Override
    public ItemDto edit(Long itemId, Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        if (userRepository.findByIdIs(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return mapper.itemToDto(itemRepository.save(mapper.itemToEntity(userId, itemDto, itemId)));
    }
}
