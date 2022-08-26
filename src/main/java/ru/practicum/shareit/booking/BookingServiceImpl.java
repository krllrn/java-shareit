package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final Mapper mapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository, UserService userService, Mapper mapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public BookingDto addBooking(Long bookerId, BookingDto bookingDto) {
        userService.checkUser(bookerId);
        if (itemRepository.findByIdIs(bookingDto.getItem().getId()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found!");
        }
        bookingDto.setStatus(BookingState.WAITING);
        return mapper.bookingToDto(bookingRepository.save(mapper.bookingDtoToEntity(bookingDto, bookerId)));
    }

    @Override
    public BookingDto updateBooking(String approved, Long bookingId, Long userId) {
        userService.checkUser(userId);
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

    @Override
    public BookingDto getInfoAboutBooking(Long userId, Long bookingId) {
        userService.checkUser(userId);
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

    @Override
    public List<BookingDto> getUserBookings(Long bookerId, String state, Integer from, Integer size) {
        userService.checkUser(bookerId);
        if (size == null || from == null) {
            from = 0;
            size = bookingRepository.findAll().size();
        }
        if (from < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect parameters FROM or SIZE!");
        }
        Pageable page = PageRequest.of(from/size, size);
        List<Booking> bookingList;
        switch (state) {
            case ("ALL"):
                bookingList = bookingRepository.findAllByBookerId(bookerId, page);
                break;
            case ("CURRENT"):
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterAndEndBefore(bookerId, LocalDateTime.now(), page);
                break;
            case ("PAST"):
                bookingList = bookingRepository.findAllByBookerIdInPast(bookerId, LocalDateTime.now(), page);
                break;
            case ("FUTURE"):
                bookingList = bookingRepository.findAllByBookerIdInFuture(bookerId, LocalDateTime.now(), page);
                break;
            case ("WAITING"):
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingState.WAITING, page);
                break;
            case ("REJECTED"):
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingState.REJECTED, page);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForUserItems(String state, Long userId, Integer from, Integer size) {
        userService.checkUser(userId);
        if (size == null || from == null) {
            from = 0;
            size = bookingRepository.findAll().size();
        }
        if (from < 0 || size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect parameters FROM or SIZE!");
        }
        Pageable page = PageRequest.of(from/size, size);
        List<Booking> bookingList;
        List<Item> userItemList = itemRepository.findByUserIdContaining(userId);
        if (userItemList.size() < 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User don't have enough items!");
        }
        switch (state) {
            case ("ALL"):
                bookingList = bookingRepository.findByItemOwnerId(userId, page);
                break;
            case ("CURRENT"):
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndBefore(userId, LocalDateTime.now(), page);
                break;
            case ("PAST"):
                bookingList = bookingRepository.findAllByItemOwnerIdInPast(userId, LocalDateTime.now(), page);
                break;
            case ("FUTURE"):
                bookingList = bookingRepository.findAllByItemOwnerIdInFuture(userId, LocalDateTime.now(), page);
                break;
            case ("WAITING"):
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingState.WAITING, page);
                break;
            case ("REJECTED"):
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingState.REJECTED, page);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream()
                .map(mapper::bookingToDto)
                .collect(Collectors.toList());
    }
}
