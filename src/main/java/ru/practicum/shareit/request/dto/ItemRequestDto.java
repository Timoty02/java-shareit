package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    int id;
    String description;
    User requester;
    LocalDateTime created;
}
