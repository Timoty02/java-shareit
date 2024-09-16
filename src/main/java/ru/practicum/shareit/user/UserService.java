package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @Getter
    Map<Integer, User> users = new HashMap<>();
    @Getter
    Set<String> emails = new HashSet<>();
    private int id = 1;

    @Autowired
    public UserService() {
    }

    public UserDto addUser(UserDto userDto) {
        log.info("Adding user: {}", userDto);
        try {
            validate(userDto);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        User user = UserMapper.toUser(userDto);
        user.setId(id++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        log.info("User added: {}", user);
        return UserMapper.toUserDto(user);
    }

    public UserDto getUser(int id) {
        log.info("Getting user with id: {}", id);
        if (!users.containsKey(id)) {
            throw new NotFoundException("User not found");
        }
        log.info("User found: {}", users.get(id));
        return UserMapper.toUserDto(users.get(id));
    }

    public UserDto updateUser(int id, UserDto userDto) {
        log.info("Updating user with id: {}", id);
        if (!users.containsKey(id)) {
            throw new NotFoundException("User not found");
        }
        try {
            emails.remove(getUser(id).getEmail());
            validateUpdate(userDto);
            User user = users.get(id);
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            users.put(id, user);
            emails.remove(user.getEmail());
            emails.add(userDto.getEmail());
            log.info("User updated: {}", user);
            return UserMapper.toUserDto(user);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

    }

    public void deleteUser(int id) {
        log.info("Deleting user with id: {}", id);
        users.remove(id);
        log.info("User deleted");
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null || userDto.getName().isBlank()
                || userDto.getEmail().isBlank() || emails.contains(userDto.getEmail()) || !validateEmail(userDto.getEmail())) {
            throw new ValidationException("Validation exception");
        }
    }

    private void validateUpdate(UserDto userDto) {
        if ((userDto.getName() != null && userDto.getName().isBlank()) ||
                (userDto.getEmail() != null && (userDto.getEmail().isBlank() ||
                        emails.contains(userDto.getEmail()) ||
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
