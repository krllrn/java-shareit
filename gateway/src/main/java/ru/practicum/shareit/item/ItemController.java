package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_ID_HEADER_REQUEST;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                           @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Get items for USER_ID: {}; FROM: {}, SIZE: {}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader(USER_ID_HEADER_REQUEST) Long userId) {
        log.info("Get item with id: {}. USER_ID: {}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Get search with text: {}. FROM: {}, SIZE: {}", text, from, size);
        return itemClient.search(text.toLowerCase(), from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Add item with name: {}.", itemDto.getName());
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> edit(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                        @RequestBody ItemDto itemDto) {
        log.info("Edit item with id: {}. USER_ID: {}.", itemId, userId);
        return itemClient.edit(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                              @Valid @RequestBody CommentDto comment) {
        log.info("Add comment to item with id: {}. USER_ID: {}. COMMENT: {}.", itemId, userId, comment);
        return itemClient.addComment(itemId, userId, comment);
    }
}
