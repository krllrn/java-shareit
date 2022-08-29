package ru.practicum.shareit.requests;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID_HEADER_REQUEST;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    // добавить новый запрос вещи
    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USER_ID_HEADER_REQUEST) Long userRequestId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userRequestId, itemRequestDto);
    }

    // получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDto> getOwnWithResponse(@RequestHeader(USER_ID_HEADER_REQUEST) Long userRequestId) {
        return itemRequestService.getOwnWithResponse(userRequestId);
    }

    //получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                        @RequestParam(value = "from", required = false) Integer from,
                                        @RequestParam(value = "size", required = false) Integer size) {
        if (from == null && size == null) {
            return itemRequestService.getAll();
        }
        return itemRequestService.getAllWithSize(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

}
