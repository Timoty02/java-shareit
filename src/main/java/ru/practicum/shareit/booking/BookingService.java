package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateSample;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserService userService, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    public BookingDto createBooking(BookingCreateSample bookingDto, int userId) {
        log.info("Creating booking: {}", bookingDto);
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(UserMapper.toUser(userService.getUser(userId)));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new AccessDeniedException("Item is not available");
        }
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking bookingUp = bookingRepository.save(booking);
        log.info("Booking created: {}", booking);
        return BookingMapper.toBookingDto(bookingUp);
    }

    public BookingDto getBookingById(int bookingId, int userId) {
        log.info("Getting booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException("Access denied");
        }
        log.info("Booking found: {}", booking);
        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto updateBooking(int bookingId, int userId, Boolean approved) {
        log.info("Updating booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException("Access denied");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public List<BookingDto> getAllBookingsOfUser(int userId, BookingDtoStatus status) {
        log.info("Getting all bookings for user with id: {}", userId);
        List<Booking> bookings = new ArrayList<>();//bookingRepository.findAllByBookerId(userId);
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndCurrent(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndPast(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndFuture(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING.name());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED.name());
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookingsOfOwner(int userId, BookingDtoStatus status) {
        log.info("Getting all bookings for owner with id: {}", userId);
        List<Booking> bookings = new ArrayList<>();//bookingRepository.findAllByOwnerId(userId);
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndCurrent(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndPast(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndFuture(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.WAITING.name());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.REJECTED.name());
                break;
        }
        if (bookings.isEmpty()) {
            throw new NotFoundException("No bookings found");
        }
        log.info("Bookings found: {}", bookings);
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
