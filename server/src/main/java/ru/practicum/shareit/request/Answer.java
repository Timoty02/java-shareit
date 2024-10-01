package ru.practicum.shareit.request;


import ru.practicum.shareit.user.User;

public interface Answer {
    int getId();

    String getName();

    User getOwner();
}
