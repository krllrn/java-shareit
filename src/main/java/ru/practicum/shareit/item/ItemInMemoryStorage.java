package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemInMemoryStorage implements ItemStorage {

    private Map<Long, Item> items = new HashMap<>();
    private long itemsCount = 0;

    @Override
    public List<Item> getItems(long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getOwner().getId() == userId) {
                userItems.add(i);
            }
        }
        return userItems;
    }

    @Override
    public Item getItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ITEM not found");
        } else {
            return items.get(itemId);
        }
    }

    @Override
    public Item addItem(long userId, Item item) {
        item.setId(itemsCount+1);
        items.put(item.getId(), item);
        itemsCount++;
        return items.get(item.getId());
    }

    @Override
    public Item edit(long itemId, long userId, ItemDto itemDto) {
        Item itemToEdit = getItemById(itemId);
        if (itemToEdit.getOwner().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can change items");
        }
        if (itemDto.getName() != null) {
            itemToEdit.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToEdit.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToEdit.setAvailable(itemDto.getAvailable());
        }
        items.put(itemToEdit.getId(), itemToEdit);
        return itemToEdit;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void deleteAll() {
        items.clear();
    }
}
