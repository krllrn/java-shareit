package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID_HEADER_REQUEST;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //Добавление нового запроса на бронирование.
    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_ID_HEADER_REQUEST) Long bookerId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);
    }

    //Подтверждение или отклонение запроса на бронирование.
    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestParam(value = "approved") String approved, @PathVariable Long bookingId,
                                    @RequestHeader(USER_ID_HEADER_REQUEST) Long userId) {
        return bookingService.updateBooking(approved, bookingId, userId);
    }

    //Получение данных о конкретном бронировании (включая его статус).
    @GetMapping("/{bookingId}")
    public BookingDto getInfoAboutBooking(@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                          @PathVariable Long bookingId) {
        return bookingService.getInfoAboutBooking(userId, bookingId);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(USER_ID_HEADER_REQUEST) Long bookerId,
                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", required = false) Integer from,
                                            @RequestParam(value = "size", required = false) Integer size) {

        return bookingService.getUserBookings(bookerId, state, from, size);
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUserItems(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                @RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size) {
        return bookingService.getBookingsForUserItems(state, userId, from, size);
    }
}
