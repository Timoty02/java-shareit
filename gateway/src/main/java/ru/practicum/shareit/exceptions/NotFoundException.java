package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    Integer id;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, int id) {
        super(message);
        this.id = id;
    }
}
