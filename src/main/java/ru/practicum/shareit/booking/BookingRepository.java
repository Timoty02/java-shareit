package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(value = "SELECT * FROM bookings WHERE booker_id = ?1", nativeQuery = true)
    List<Booking> findAllByBookerId(int bookerId);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = ?1 AND start_date < NOW() AND end_date > NOW()", nativeQuery = true)
    List<Booking> findAllByBookerIdAndCurrent(int bookerId);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = ?1 AND end_date < NOW()", nativeQuery = true)
    List<Booking> findAllByBookerIdAndPast(int bookerId);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = ?1 AND start_date > NOW()", nativeQuery = true)
    List<Booking> findAllByBookerIdAndFuture(int bookerId);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = ?1 AND status = ?2", nativeQuery = true)
    List<Booking> findAllByBookerIdAndStatus(int bookerId, String status);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT id FROM items WHERE owner_id = ?1)", nativeQuery = true)
    List<Booking> findAllByOwnerId(int ownerId);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT id FROM items WHERE owner_id = ?1) AND start_date < NOW() AND end_date > NOW()", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndCurrent(int ownerId);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT id FROM items WHERE owner_id = ?1) AND end_date < NOW()", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndPast(int ownerId);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT id FROM items WHERE owner_id = ?1) AND start_date > NOW()", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndFuture(int ownerId);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT id FROM items WHERE owner_id = ?1) AND status = ?2", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndStatus(int ownerId, String status);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = ?1 AND item_id = ?2 AND status = ?3 AND end_date <= NOW() ", nativeQuery = true)
    List<Booking> findAllByBookerAndItemAndStatus(int userId, int itemId, String status);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 AND end_date < NOW() ORDER BY end_date DESC LIMIT 1", nativeQuery = true)
    Booking findLastBookingByItemId(int itemId);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 AND start_date > NOW() ORDER BY start_date ASC LIMIT 1", nativeQuery = true)
    Booking findNextBookingByItemId(int itemId);
}
