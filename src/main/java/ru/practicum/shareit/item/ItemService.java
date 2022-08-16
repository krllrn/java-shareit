package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> search(String text);

    Item addComment(Long itemId, Long userId, Comment comment);

    List<ItemDto> getItems(Long userId);

    ItemDto getItemById(long itemId, Long userId);

    ItemDto addItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto edit(Long itemId, Long userId, ItemDto itemDto);
}
