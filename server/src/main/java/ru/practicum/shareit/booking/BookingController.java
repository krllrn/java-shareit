package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.ShareItServer.USER_ID_HEADER_REQUEST;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_ID_HEADER_REQUEST) Long bookerId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Получен запрос /POST для добавления бронирования от пользователя с id: {}", bookerId);
        log.debug("Бронирование: {}", bookingDto);
        return bookingService.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestParam(value = "approved") String approved, @PathVariable Long bookingId,
                                    @RequestHeader(USER_ID_HEADER_REQUEST) Long userId) {
        log.debug("Получен запрос /PATCH для подтверждения/отклонения бронирования с id: {}", bookingId);
        log.debug("APPROVED: {}; USER_ID: {}", approved, userId);
        return bookingService.updateBooking(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getInfoAboutBooking(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                          @PathVariable Long bookingId) {
        log.debug("Получен запрос /GET для формирования данных о бронировании с id: {}", bookingId);
        log.debug("USER_ID: {}", userId);
        return bookingService.getInfoAboutBooking(userId, bookingId);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(USER_ID_HEADER_REQUEST) Long bookerId,
                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", required = false) Integer from,
                                            @RequestParam(value = "size", required = false) Integer size) {
        log.debug("Получен запрос /GET для формирования списка бронирований пользователя с id: {}", bookerId);
        log.debug("STATE: {}; FROM: {}; SIZE: {}", state, from, size);
        return bookingService.getUserBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUserItems(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size) {
        log.debug("Получен запрос /GET для формирования списка бронирований для вещей пользователя с id: {}", userId);
        log.debug("STATE: {}; FROM: {}; SIZE: {}", state, from, size);
        return bookingService.getBookingsForUserItems(state, userId, from, size);
    }
}
