package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    int id;
    String name;
    String description;
    boolean available;
    String owner;
    ItemRequest request;
}
