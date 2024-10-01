package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateSample;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer bookingId) {
        log.info("Get booking: {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID) Integer userId, @RequestBody @Valid BookingCreateSample bookingDto) {
        log.info("Create booking: {}", bookingDto);
        BookingDto booking = bookingService.addBooking(bookingDto, userId);
        log.info("Booking created: {}", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(SHARER_USER_ID) Integer userId,
                                    @PathVariable Integer bookingId, @RequestParam Boolean approved) {
        log.info("Update booking: {}", bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsOfUser(@RequestHeader(SHARER_USER_ID) Integer userId,
                                                 @RequestParam(defaultValue = "ALL") BookingDtoStatus state) {
        log.info("Get all bookings of user: {}", userId);
        return bookingService.getAllBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfOwner(@RequestHeader(SHARER_USER_ID) Integer userId,
                                                  @RequestParam(defaultValue = "ALL") BookingDtoStatus state) {
        log.info("Get all bookings of owner: {}", userId);
        return bookingService.getAllBookingsOfOwner(userId, state);
    }
}
