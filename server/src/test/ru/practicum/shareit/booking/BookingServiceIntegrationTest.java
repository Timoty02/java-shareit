package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateSample;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto testItem;
    private BookingCreateSample bookingCreateSample;
    private BookingDto retrievedBooking;

    @BeforeEach
    void setUp() {
        // Create owner
        UserDto ownerUser = new UserDto();
        ownerUser.setName("Owner");
        ownerUser.setEmail("owner@example.com");
        owner = userService.addUser(ownerUser);

        // Create booker
        UserDto bookerUser = new UserDto();
        bookerUser.setName("Booker");
        bookerUser.setEmail("booker@example.com");
        booker = userService.addUser(bookerUser);

        // Create test item
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        testItem = itemService.addItem(itemDto, owner.getId());

        // Create booking sample
        bookingCreateSample = new BookingCreateSample();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);
        bookingCreateSample.setStart(start);
        bookingCreateSample.setEnd(end);
        bookingCreateSample.setItemId(testItem.getId());
    }

    @Test
    void testCreateAndRetrieveBooking() {
        // Create a booking
        BookingDto adddBooking = bookingService.addBooking(bookingCreateSample, booker.getId());

        // Verify the booking was addd
        assertEquals(bookingCreateSample.getStart(), adddBooking.getStart());
        assertEquals(bookingCreateSample.getEnd(), adddBooking.getEnd());
        assertEquals(testItem.getId(), adddBooking.getItem().getId());
        assertEquals(booker.getId(), adddBooking.getBooker().getId());
        assertEquals(BookingStatus.WAITING, adddBooking.getStatus());

        // Retrieve the booking
        retrievedBooking = bookingService.getBookingById(adddBooking.getId(), booker.getId());

        // Verify the retrieved booking matches the addd booking
        assertEquals(adddBooking.getId(), retrievedBooking.getId());
        assertEquals(adddBooking.getStart(), retrievedBooking.getStart());
        assertEquals(adddBooking.getEnd(), retrievedBooking.getEnd());
        assertEquals(adddBooking.getItem().getId(), retrievedBooking.getItem().getId());
        assertEquals(adddBooking.getBooker().getId(), retrievedBooking.getBooker().getId());
        assertEquals(adddBooking.getStatus(), retrievedBooking.getStatus());
    }

    @Test
    void testApproveBooking() {
        // Create a booking
        BookingDto adddBooking = bookingService.addBooking(bookingCreateSample, booker.getId());

        // Approve the booking
        BookingDto approvedBooking = bookingService.updateBooking(adddBooking.getId(), owner.getId(), true);

        // Verify the booking was approved
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void testRejectBooking() {
        // Create a booking
        BookingDto adddBooking = bookingService.addBooking(bookingCreateSample, booker.getId());

        // Reject the booking
        BookingDto rejectedBooking = bookingService.updateBooking(adddBooking.getId(), owner.getId(), false);

        // Verify the booking was rejected
        assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
    }

    @Test
    void testGetBookingsForBooker() {
        // Create multiple bookings for the booker
        bookingService.addBooking(bookingCreateSample, booker.getId());
        bookingService.addBooking(bookingCreateSample, booker.getId());

        // Retrieve all bookings for the booker
        List<BookingDto> bookerBookings = bookingService.getAllBookingsOfUser(booker.getId(), BookingDtoStatus.ALL);
        // Verify the number of bookings
        assertEquals(2, bookerBookings.size());
    }

    @Test
    void testGetBookingsForOwner() {
        // Create multiple bookings for the owner's item
        bookingService.addBooking(bookingCreateSample, booker.getId());
        bookingService.addBooking(bookingCreateSample, booker.getId());

        // Retrieve all bookings for the owner
        List<BookingDto> ownerBookings = bookingService.getAllBookingsOfOwner(owner.getId(), BookingDtoStatus.ALL);

        // Verify the number of bookings
        assertEquals(2, ownerBookings.size());
    }
}
