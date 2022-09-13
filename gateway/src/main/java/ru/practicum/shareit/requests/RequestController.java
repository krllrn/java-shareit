package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_ID_HEADER_REQUEST;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID_HEADER_REQUEST) Long userRequestId,
                                             @Valid @RequestBody RequestDto itemRequestDto) {
        log.info("Create new item request from user with id: {}.", userRequestId);
        return requestClient.addRequest(userRequestId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnWithResponse(@RequestHeader(USER_ID_HEADER_REQUEST) Long userRequestId) {
        log.info("Get requests from user with id: {}.", userRequestId);
        return requestClient.getOwnWithResponse(userRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                       @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                       @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Get all requests. USER_ID: {}; FROM: {}, SIZE: {}", userId, from, size);
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId, @PathVariable Long requestId) {
        log.info("Get info about request with id: {}. USER_ID: {}", requestId, userId);
        return requestClient.getById(userId, requestId);
    }
}
