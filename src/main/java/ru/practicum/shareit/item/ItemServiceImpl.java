package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> searchedItems = new ArrayList<>();
        for (Item i : itemRepository.findAll()) {
            if ((i.getName().toLowerCase().contains(text) || i.getDescription().toLowerCase().contains(text)) && i.getAvailable()) {
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
}
