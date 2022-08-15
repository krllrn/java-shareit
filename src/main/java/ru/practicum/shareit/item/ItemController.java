package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final Mapper mapper;

    @Autowired
    public ItemController(ItemRepository itemRepository, UserRepository userRepository,
                          BookingRepository bookingRepository, ItemService itemService, Mapper mapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID.");
        }
        return itemRepository.findByUserIdContaining(userId).stream()
                .map(mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (itemRepository.findByIdIs(itemId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found.");
        }
        if (userId != itemRepository.findByIdIs(itemId).getOwner().getId()) {
            return mapper.itemToDtoWoBookings(itemRepository.findByIdIs(itemId));
        }
        return mapper.itemToDto(itemRepository.findByIdIs(itemId));
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
    public ItemDto addItem(@PathVariable (required = false) Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        if (userRepository.findByIdIs(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return mapper.itemToDto(itemRepository.save(mapper.itemToEntity(userId, itemDto, itemId)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID. Only owner have access");
        }
        if (userRepository.findByIdIs(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return mapper.itemToDto(itemRepository.save(mapper.itemToEntity(userId, itemDto, itemId)));
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody Comment comment) {
        Booking correctBooking = bookingRepository.findByBookerIdAndItemIdAndStartDateCorrectOrStatus(userId, itemId,
                LocalDateTime.now(), BookingState.REJECTED);
        if (correctBooking == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user didn't take this item.");
        }
        return itemService.addComment(itemId, userId, comment).getComment();
    }

    @ExceptionHandler({EntityNotFoundException.class})
    void handleEntityNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({NonUniqueResultException.class})
    void handleEntityNonUnique(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
