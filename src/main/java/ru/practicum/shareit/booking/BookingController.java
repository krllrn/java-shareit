package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Mapper mapper;

    @Autowired
    public BookingController(BookingRepository bookingRepository, UserRepository userRepository,
                             ItemRepository itemRepository, Mapper mapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    //Добавление нового запроса на бронирование.
    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        checkUser(bookerId);
        if (itemRepository.findByIdIs(bookingDto.getItem().getId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found!");
        }
        bookingDto.setStatus(BookingState.WAITING);
        return mapper.bookingToDto(bookingRepository.save(mapper.bookingDtoToEntity(bookingDto, bookerId)));
    }

    //Подтверждение или отклонение запроса на бронирование.
    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestParam(value = "approved") String approved, @PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findByIdIs(bookingId);
        if (booking.getItemOwnerId() != userId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Only owner have access.");
        }
        if (booking.getStatus().equals(BookingState.APPROVED) && approved.equals("true")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking already approved.");
        }
        switch (approved) {
            case ("true"):
                booking.setStatus(BookingState.APPROVED);
                break;
            case ("false"):
                booking.setStatus(BookingState.REJECTED);
                break;
        }
        return mapper.bookingToDto(bookingRepository.save(booking));
    }

    //Получение данных о конкретном бронировании (включая его статус).
    @GetMapping("/{bookingId}")
    public BookingDto getInfoAboutBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId) {
        checkUser(userId);
        if (bookingRepository.findByIdIs(bookingId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found!");
        }
        Booking booking = bookingRepository.findByIdIs(bookingId);
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItemOwnerId();
        if (!(bookerId == userId || ownerId == userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Only owner or booker have access");
        }
        return mapper.bookingToDto(booking);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        checkUser(bookerId);
        List<Booking> bookingList = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookingList = bookingRepository.findAllByBookerId(bookerId);
                break;
            case ("CURRENT"):
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterAndEndBefore(bookerId, LocalDateTime.now());
                break;
            case ("PAST"):
                bookingList = bookingRepository.findAllByBookerIdInPast(bookerId, LocalDateTime.now());
                break;
            case ("FUTURE"):
                bookingList = bookingRepository.findAllByBookerIdInFuture(bookerId, LocalDateTime.now());
                break;
            case ("WAITING"):
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingState.WAITING);
                break;
            case ("REJECTED"):
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingState.REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUserItems(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        checkUser(userId);
        List<Booking> bookingList = new ArrayList<>();
        List<Item> userItemList = itemRepository.findByUserIdContaining(userId);
        if (userItemList.size() < 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User don't have enough items!");
        }
        switch (state) {
            case ("ALL"):
                bookingList = bookingRepository.findByItemOwnerId(userId);
                break;
            case ("CURRENT"):
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndBefore(userId, LocalDateTime.now());
                break;
            case ("PAST"):
                bookingList = bookingRepository.findAllByItemOwnerIdInPast(userId, LocalDateTime.now());
                break;
            case ("FUTURE"):
                bookingList = bookingRepository.findAllByItemOwnerIdInFuture(userId, LocalDateTime.now());
                break;
            case ("WAITING"):
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingState.WAITING);
                break;
            case ("REJECTED"):
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingState.REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
    }

    private void checkUser(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No USER_ID.");
        }
        if (userRepository.findByIdIs(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }

    @ExceptionHandler({EntityNotFoundException.class})
    void handleEntityNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
