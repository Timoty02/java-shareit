package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
public class ItemDto {
    int id;
    String name;
    String description;
    boolean available;
    String owner;
    int requestId;

    public ItemDto(String name, String description, boolean available, int requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
