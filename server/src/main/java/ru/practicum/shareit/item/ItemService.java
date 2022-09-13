package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> search(String text, Integer from, Integer size);

    Item addComment(Long itemId, Long userId, Comment comment);

    List<ItemDto> getItems(Long userId, Integer from, Integer size);

    ItemDto getItemById(long itemId, Long userId);

    ItemDto addItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto edit(Long itemId, Long userId, ItemDto itemDto);
}
