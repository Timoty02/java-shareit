package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Get users");
        List<UserDto> users= userService.getUsers();
        log.info("Users: {}", users);
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") int id) {
        log.info("Get user: {}", id);
        UserDto user = userService.getUser(id);
        log.info("User: {}", user);
        return user;
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("Add user: {}", userDto);
        UserDto user = userService.addUser(userDto);
        log.info("User: {}", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") int id, @RequestBody UserDto userDto) {
        log.info("Update user: {}", userDto);
        UserDto user = userService.updateUser(id, userDto);
        log.info("User: {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") int id) {
        log.info("Delete user: {}", id);
        userService.deleteUser(id);
        log.info("User deleted");
    }
}
