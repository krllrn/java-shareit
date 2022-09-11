package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.ItemRequestServiceImpl;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureTestDatabase
public class ItemRequestServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private Mapper mapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final User user = new User(1L, "test1", "test1@email.com");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto("Test description");
    private final ItemRequest itemRequest = new ItemRequest(1L, "Test description", LocalDateTime.now(), user);

    @Test
    public void testAddRequest() {
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService,
                mapper);
        Mockito.when(mapper.requestDtoToEntity(any(),any()))
                .thenReturn(itemRequest);
        Mockito.when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        Mockito.when(mapper.requestToDto(any()))
                .thenReturn(itemRequestDto);

        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestService.addRequest(user.getId(),
                itemRequestDto).getDescription());
    }

    @Test
    public void testGetOwnWithResponse() {
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService,
                mapper);
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        Mockito.when(itemRequestRepository.findByRequestOwnerIdContaining(any()))
                .thenReturn(itemRequestList);
        Assertions.assertEquals(itemRequestList.size(), itemRequestService.getOwnWithResponse(user.getId()).size());
    }

    @Test
    public void testGetAllWithSizeWrong() {
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService,
                mapper);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemRequestService.getAllWithSize(0L, -1, 0);
        });
        Assertions.assertTrue(exception.getMessage().contains("Incorrect parameters FROM or SIZE!"));
    }

    @Test
    public void testGetById() {
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService,
                mapper);
        Mockito.when(itemRequestRepository.findByIdIs(any()))
                .thenReturn(itemRequest);
        Mockito.when(mapper.requestToDto(any()))
                .thenReturn(itemRequestDto);

        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestService.getById(user.getId(),
                itemRequest.getId()).getDescription());
    }

    @Test
    public void testGetByIdWrong() {
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService,
                mapper);
        Mockito.when(itemRequestRepository.findByIdIs(anyLong()))
                .thenReturn(null);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            itemRequestService.getById(1L, 1L);
        });
        Assertions.assertTrue(exception.getMessage().contains("Item request not found!"));
    }

    @Test
    public void testGetAll() {
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService,
                mapper);
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        Mockito.when(itemRequestRepository.findAll())
                .thenReturn(itemRequestList);
        Assertions.assertEquals(itemRequestList.size(), itemRequestService.getAll().size());
    }
}
