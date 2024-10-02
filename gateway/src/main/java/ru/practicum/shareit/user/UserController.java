package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<Object> getUser(@PathVariable("user-id") @Positive int id) {
        log.info("Get user: {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated UserDto userDto) {
        log.info("Add user: {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{user-id}")
    public ResponseEntity<Object> updateUser(@PathVariable("user-id") @Positive int id, @RequestBody UserUpdateDto userDto) {
        log.info("Update user: {}", userDto);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{user-id}")
    public void deleteUser(@PathVariable("user-id") @Positive int id) {
        log.info("Delete user: {}", id);
        userClient.deleteUser(id);
    }
}
