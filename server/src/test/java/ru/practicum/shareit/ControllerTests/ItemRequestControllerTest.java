package ru.practicum.shareit.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto("Test item request");

    private final ItemRequestDto itemRequestDto2 = new ItemRequestDto("Test2 item request");

    @Test
    public void testAddNewRequest() throws Exception {
        when(itemRequestService.addRequest(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                    .header("X-Sharer-User-Id", 1)
                    .content(mapper.writeValueAsString(itemRequestDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void testGetRequests() throws Exception {
        List<ItemRequestDto> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequestDto);
        itemRequestList.add(itemRequestDto2);
        when(itemRequestService.getOwnWithResponse(any()))
                .thenReturn(itemRequestList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    public void testGetRequestById() throws Exception {
        when(itemRequestService.getById(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void testGetAllRequestsNullFromSize() throws Exception {
        List<ItemRequestDto> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequestDto);
        itemRequestList.add(itemRequestDto2);
        when(itemRequestService.getAll())
                .thenReturn(itemRequestList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    public void testGetAllRequestsWithFromSize() throws Exception {
        List<ItemRequestDto> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequestDto);
        itemRequestList.add(itemRequestDto2);
        when(itemRequestService.getAllWithSize(any(), any(), any()))
                .thenReturn(itemRequestList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }
}
