package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AnswerDto;

public class AnswerMapper {
    public static AnswerDto toAnwserDto(Answer answer) {
        return new AnswerDto(
                answer.getId(),
                answer.getName(),
                answer.getOwner().getId()
        );
    }
}
