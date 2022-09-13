package ru.practicum.shareit.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserId;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private final ItemShort itemShort = new ItemShort(1L);
    private final BookingDto bookingDto = new BookingDto(itemShort, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

    private final BookingDto bookingDto2 = new BookingDto(new ItemShort(1L), LocalDateTime.now(), LocalDateTime.now().plusDays(1));

    @Test
    public void testAddBooking() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingState.WAITING);
        bookingDto.setBooker(new UserId(1L));
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(7));

        when(bookingService.addBooking(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                    .header("X-Sharer-User-Id", 1L)
                    .content(mapper.writeValueAsString(bookingDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testUpdateBooking() throws Exception {
        bookingDto.setStatus(BookingState.APPROVED);
        when(bookingService.updateBooking(any(), any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", String.valueOf(true))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void testGetInfoAboutBooking() throws Exception {
        bookingDto.setStatus(BookingState.APPROVED);
        when(bookingService.getInfoAboutBooking(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                    .header("X-Sharer-User-Id", 1L)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void testGetItems() throws Exception {
        bookingDto.setStatus(BookingState.APPROVED);
        bookingDto2.setStatus(BookingState.REJECTED);
        List<BookingDto> bookingList = new ArrayList<>();
        bookingList.add(bookingDto);
        bookingList.add(bookingDto2);
        when(bookingService.getUserBookings(any(), any(), any(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }

    @Test
    public void testGetItemsException() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS"));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "TEST")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBookingsForUserItemsException() throws Exception {
        when(bookingService.getBookingsForUserItems(anyString(), anyLong(), anyInt(), anyInt()))
                .thenThrow(new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS"));

        mvc.perform(get("/bookings/owner")
                        .param("state", "TEST")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
