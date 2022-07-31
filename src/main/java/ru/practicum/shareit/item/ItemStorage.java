package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItems(long userId);
    Item getItemById(long itemId);
    Item addItem(long userId, Item item);
    Item edit(long itemId, long userId, ItemDto itemDto);
    List<Item> getAllItems();
    void deleteAll();
}
