package ru.practicum.shareit.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FutureOrPresentWithGracePeriodValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentWithGracePeriod {
    String message() default "Дата должна быть в будущем или настоящем с учетом задержки";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long gracePeriod() default 5000; // Задержка в миллисекундах (например, 5000 мс = 5 секунд)
}
