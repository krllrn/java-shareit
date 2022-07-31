package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemStorage itemStorage;

    @Autowired
    ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> searchedItems = new ArrayList<>();
        for (Item i : itemStorage.getAllItems()) {
            if ((i.getName().toLowerCase().contains(text) || i.getDescription().toLowerCase().contains(text)) && i.getAvailable()) {
                searchedItems.add(i);
            }
        }
        return searchedItems;
    }
}
