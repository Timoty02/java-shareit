package ru.practicum.shareit.user;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    @Getter
    Map<Integer, User> users = new HashMap<>();
    @Getter
    Set<String> emails = new HashSet<>();
    private int id = 1;
    @Autowired
    public UserService() {
    }
    public UserDto addUser(UserDto userDto) {
        try {
            validate(userDto);
        } catch (ValidationException e){
            throw new ValidationException(e.getMessage());
        }
        User user = UserMapper.toUser(userDto);
        user.setId(id++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }
    public UserDto getUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User not found");
        }
        return UserMapper.toUserDto(users.get(id));
    }

    public UserDto updateUser(int id, UserDto userDto) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User not found");
        }
        try {
            validate(userDto);
            User user = users.get(id);
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            users.put(id, user);
            emails.remove(user.getEmail());
            emails.add(userDto.getEmail());
            return UserMapper.toUserDto(user);
        } catch (ValidationException e){
            throw new ValidationException(e.getMessage());
        }

    }
    public void deleteUser(int id) {
        users.remove(id);
    }
    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null || userDto.getName().isBlank()
                || userDto.getEmail().isBlank() || emails.contains(userDto.getEmail()) ) {
            throw new ValidationException("Email and name cannot be null");
        }
    }

}
