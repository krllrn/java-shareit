package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long bookerId,BookingDto bookingDto);

    BookingDto updateBooking(String approved, Long bookingId, Long userId);

    BookingDto getInfoAboutBooking(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long bookerId, String state, Integer from, Integer size);

    List<BookingDto> getBookingsForUserItems(String state, Long userId, Integer from, Integer size);
}
