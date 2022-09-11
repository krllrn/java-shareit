package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userRequestId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwnWithResponse(Long userRequestId);

    List<ItemRequestDto> getAllWithSize(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAll();

    ItemRequestDto getById(Long userId, Long requestId);
}
