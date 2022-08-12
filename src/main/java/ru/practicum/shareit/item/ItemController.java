package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final Mapper mapper;

    @Autowired
    public ItemController(ItemRepository itemRepository, ItemService itemService, Mapper mapper) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        return itemRepository.findByUserIdContaining(userId).stream()
                .map(mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return mapper.itemToDto(itemRepository.getReferenceById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemService.search(text.toLowerCase()).stream()
                .map(mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@PathVariable (required = false) Long itemId, @RequestHeader("X-Sharer-User-Id") String userId,
                           @Valid @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        return mapper.itemToDto(itemRepository.save(mapper.itemToEntity(Long.parseLong(userId), itemDto, itemId)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") String userId,
                        @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        Item item = itemRepository.save(mapper.itemToEntity(Long.parseLong(userId), itemDto, itemId));
        item.setId(itemId);
        return mapper.itemToDto(item);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    void handleEntityNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
