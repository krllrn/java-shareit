package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
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
        // Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи,
        // название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
        // Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
        return itemRequestService.getOwnWithResponse(userRequestId);
    }

    //получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                        @RequestParam(value = "from", required = false) Integer from,
                                        @RequestParam(value = "size", required = false) Integer size) {
        // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
        // Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
        // Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
        if (from == null && size == null) {
            return itemRequestService.getAll();
        }
        return itemRequestService.getAllWithSize(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId, @PathVariable Long requestId) {
        // в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.

        return itemRequestService.getById(userId, requestId);
    }

}
