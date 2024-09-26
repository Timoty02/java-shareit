package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BookingCreateSample {
    @NotNull
    @Future
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
    int itemId;

    public BookingCreateSample(LocalDateTime start, LocalDateTime end, int itemId) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        validate();
    }

    private void validate() {
        if (start.isEqual(end)) {
            throw new ValidationException("Start date must not be equal to end date");
        }
    }
}
