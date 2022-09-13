package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_ID_HEADER_REQUEST;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Create booking for item with id: {}, userId: {}", requestDto.getItemId(), userId);
		return bookingClient.addBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestParam(value = "approved") String approved, @PathVariable Long bookingId,
												@RequestHeader(USER_ID_HEADER_REQUEST) Long userId) {
		log.info("Patch booking with id: {}, state: {}, user id: {}", bookingId, approved, userId);
		return bookingClient.updateBooking(approved, bookingId, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getInfoAboutBooking(@RequestHeader(USER_ID_HEADER_REQUEST) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getInfoAboutBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_HEADER_REQUEST) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsForUserItems(
			@RequestParam(value = "state", required = false, defaultValue = "ALL") String stateParam,
			@RequestHeader(USER_ID_HEADER_REQUEST) Long userId,
			@PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsForUserItems(state, userId, from, size);
	}
}
