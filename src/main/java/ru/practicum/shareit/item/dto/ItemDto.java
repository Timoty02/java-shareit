package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer requestId;

    public ItemDto(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDto(String name, String description, boolean available, int requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
