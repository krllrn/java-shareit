package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID_HEADER_REQUEST;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USER_ID_HEADER_REQUEST) Long userRequestId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Получен запрос /POST для создания нового запроса вещи от пользователя с id: {}.", userRequestId);
        log.debug("Тело запроса: {}.", itemRequestDto);
        return itemRequestService.addRequest(userRequestId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnWithResponse(@RequestHeader(USER_ID_HEADER_REQUEST) Long userRequestId) {
        log.debug("Получен запрос /GET для формирования списка запросов пользователя с id: {}.", userRequestId);
        return itemRequestService.getOwnWithResponse(userRequestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                        @RequestParam(value = "from", required = false) Integer from,
                                        @RequestParam(value = "size", required = false) Integer size) {
        log.debug("Получен запрос /GET для формирования списка всех запросов.");
        log.debug("USER_ID: {}; FROM: {}, SIZE: {}", userId, from, size);
        if (from == null && size == null) {
            return itemRequestService.getAll();
        }
        return itemRequestService.getAllWithSize(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId, @PathVariable Long requestId) {
        log.debug("Получен запрос /GET для формирования данных об запросе с id: {}.", requestId);
        log.debug("USER_ID: {}", userId);
        return itemRequestService.getById(userId, requestId);
    }

}
