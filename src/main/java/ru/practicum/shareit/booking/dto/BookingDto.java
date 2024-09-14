package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    int id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
}
