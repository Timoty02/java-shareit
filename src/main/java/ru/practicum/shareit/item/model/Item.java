package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    int id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;
}
