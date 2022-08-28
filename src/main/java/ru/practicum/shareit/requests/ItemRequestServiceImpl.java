package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final Mapper mapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserService userService, Mapper mapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public ItemRequestDto addRequest(Long userRequestId, ItemRequestDto itemRequestDto) {
        userService.checkUser(userRequestId);
        return mapper.requestToDto(itemRequestRepository.save(mapper.requestDtoToEntity(userRequestId, itemRequestDto)));
    }

    @Override
    public List<ItemRequestDto> getOwnWithResponse(Long userRequestId) {
        userService.checkUser(userRequestId);
        return itemRequestRepository.findByRequestOwnerIdContaining(userRequestId).stream()
                .map(mapper::requestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllWithSize(Long userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect parameters FROM or SIZE!");
        }
        Sort sortByCreate = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from/size, size, sortByCreate);
        return itemRequestRepository.findAll(page).getContent().stream()
                    .filter(i -> i.getReqOwnerId().getId() != userId)
                    .map(mapper::requestToDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userService.checkUser(userId);
        if (itemRequestRepository.findByIdIs(requestId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item request not found!");
        }
        return mapper.requestToDto(itemRequestRepository.findByIdIs(requestId));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return itemRequestRepository.findAll().stream()
                .map(mapper::requestToDto)
                .collect(Collectors.toList());
    }
}
