package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.ShareItServer.USER_ID_HEADER_REQUEST;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                  @RequestParam(value = "from", required = false) Integer from,
                                  @RequestParam(value = "size", required = false) Integer size) {
        log.debug("Получен запрос /GET для формирования списка вещей.");
        log.debug("USER_ID: {}; FROM: {}, SIZE: {}", userId, from, size);
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER_REQUEST) Long userId) {
        log.debug("Получен запрос /GET для формирования данных об одной вещи с id: {}.", itemId);
        log.debug("USER_ID: {}", userId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(value = "from", required = false) Integer from,
                                @RequestParam(value = "size", required = false) Integer size) {
        log.debug("Получен запрос /GET для поиска вещей, содержащих текст: {}.", text);
        log.debug("FROM: {}, SIZE: {}", from, size);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemService.search(text.toLowerCase(), from, size);
    }

    @PostMapping
    public ItemDto addItem(@PathVariable (required = false) Long itemId, @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос /POST для добавления вещи с id: {}.", itemId);
        log.debug("USER_ID: {}, ITEM_DTO: {}", userId, itemDto);
        return itemService.addItem(itemId, userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                        @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос /PATCH для обновления данных вещи с id: {}.", itemId);
        log.debug("USER_ID: {}. Данные для обновления: {}.", userId, itemDto);
        return itemService.edit(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                              @Valid @RequestBody Comment comment) {
        log.debug("Получен запрос /POST для добавления комментария к вещи с id: {}.", itemId);
        log.debug("USER_ID: {}. Комментарий: {}.", userId, comment);
        return itemService.addComment(itemId, userId, comment).getComment();
    }

    @ExceptionHandler({EntityNotFoundException.class})
    void handleEntityNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
