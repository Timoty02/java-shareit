package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    @GetMapping
    public String getUsers() {
        return "Get users";
    }

    @GetMapping("/{userId}")
    public String getUser() {
        return "Get user";
    }
}
