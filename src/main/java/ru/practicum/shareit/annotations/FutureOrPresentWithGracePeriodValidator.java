package ru.practicum.shareit.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class FutureOrPresentWithGracePeriodValidator implements ConstraintValidator<FutureOrPresentWithGracePeriod, LocalDateTime> {
    private long gracePeriod;

    @Override
    public void initialize(FutureOrPresentWithGracePeriod constraintAnnotation) {
        this.gracePeriod = constraintAnnotation.gracePeriod();
    }

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
        if (dateTime == null) {
            return true;
        }
        LocalDateTime nowWithGrace = LocalDateTime.now().minusSeconds(5); // Преобразуем миллисекунды в наносекунды
        return !dateTime.isBefore(nowWithGrace);
    }
}
