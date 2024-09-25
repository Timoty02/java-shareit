package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto addUser(UserDto userDto) {
        log.info("Adding user: {}", userDto);
        try {
            validate(userDto);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        User user = UserMapper.toUser(userDto);
        User userAdd = repository.save(user);
        log.info("User added: {}", userAdd);
        return UserMapper.toUserDto(userAdd);
    }
    public List<UserDto> getUsers() {
        log.info("Getting all users");
        List<User> users = repository.findAll();
        log.info("Users found: {}", users);
        return users.stream().map(UserMapper::toUserDto).toList();
    }

    public UserDto getUser(int id) {
        log.info("Getting user with id: {}", id);
        Optional<User> mayBeUser = repository.findById(id);
        User user = mayBeUser.orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User found: {}", user);
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(int id, UserDto userDto) {
        log.info("Updating user with id: {}", id);
        try {
            validateUpdate(userDto);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        Optional<User> mayBeUser = repository.findById(id);
        User user = mayBeUser.orElseThrow(() -> new NotFoundException("User not found"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User userUp = repository.save(user);
        log.info("User updated: {}", userUp);
        return UserMapper.toUserDto(userUp);
    }

    public void deleteUser(int id) {
        log.info("Deleting user with id: {}", id);
        repository.deleteById(id);
        log.info("User deleted");
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null || userDto.getName().isBlank()
                || userDto.getEmail().isBlank() || !repository.findByEmailContaining(userDto.getEmail()).isEmpty() || !validateEmail(userDto.getEmail())) {
            throw new ValidationException("Validation exception");
        }
    }

    private void validateUpdate(UserDto userDto) {
        if ((userDto.getName() != null && userDto.getName().isBlank()) ||
                (userDto.getEmail() != null && (userDto.getEmail().isBlank() ||
                        !repository.findByEmailContaining(userDto.getEmail()).isEmpty() ||
                        !validateEmail(userDto.getEmail())))) {
            throw new ValidationException("Email and name cannot be blank");
        }
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
