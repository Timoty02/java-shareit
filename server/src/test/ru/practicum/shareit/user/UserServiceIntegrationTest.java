package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testAddUser() {
        UserDto userDto = new UserDto(0, "John Doe", "john@example.com");
        UserDto savedUser = userService.addUser(userDto);

        assertNotNull(savedUser.getId());
        assertEquals(userDto.getName(), savedUser.getName());
        assertEquals(userDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void testAddUserWithInvalidEmail() {
        UserDto userDto = new UserDto(0, "John Doe", "invalid-email");
        assertThrows(ValidationException.class, () -> userService.addUser(userDto));
    }

    @Test
    void testGetUserById() {
        UserDto userDto = new UserDto(0, "Jane Doe", "jane@example.com");
        UserDto savedUser = userService.addUser(userDto);

        UserDto retrievedUser = userService.getUser(savedUser.getId());
        assertEquals(savedUser, retrievedUser);
    }

    @Test
    void testGetUserByIdNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto(0, "Alice", "alice@example.com");
        UserDto savedUser = userService.addUser(userDto);

        UserDto updateDto = new UserDto(savedUser.getId(), "Alice Updated", "alice.updated@example.com");
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertEquals(updateDto.getName(), updatedUser.getName());
        assertEquals(updateDto.getEmail(), updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        UserDto userDto = new UserDto(0, "Bob", "bob@example.com");
        UserDto savedUser = userService.addUser(userDto);

        userService.deleteUser(savedUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(savedUser.getId()));
    }

    @Test
    void testGetAllUsers() {
        UserDto user1 = new UserDto(0, "User1", "user1@example.com");
        UserDto user2 = new UserDto(0, "User2", "user2@example.com");

        userService.addUser(user1);
        userService.addUser(user2);

        List<UserDto> allUsers = userService.getUsers();

        assertTrue(allUsers.size() >= 2);
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals(user1.getEmail())));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals(user2.getEmail())));
    }
}
