package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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
}
