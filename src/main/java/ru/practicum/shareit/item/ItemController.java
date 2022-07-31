package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemStorage itemStorage;
    private ItemService itemService;
    private Mapper mapper;

    @Autowired
    public ItemController(ItemStorage itemStorage, ItemService itemService, Mapper mapper) {
        this.itemStorage = itemStorage;
        this.itemService = itemService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") String userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        List<Item> itemList = itemStorage.getItems(Long.parseLong(userId));
        List<ItemDto> itemDtoList = itemList.stream()
                .map(item -> mapper.itemToDto(item))
                .collect(Collectors.toList());
        return itemDtoList;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return mapper.itemToDto(itemStorage.getItemById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemService.search(text.toLowerCase()).stream()
                .map(item -> mapper.itemToDto(item))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        Item toEntity = mapper.itemToEntity(userId, itemDto);
        return mapper.itemToDto(itemStorage.addItem(userId, toEntity));
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") String userId, @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        return mapper.itemToDto(itemStorage.edit(itemId, Long.parseLong(userId), itemDto));
    }
}