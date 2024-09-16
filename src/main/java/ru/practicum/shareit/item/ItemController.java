package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Get all items of user: {}", userId);
        try {
            userService.getUser(userId);
        } catch (Exception e) {
            throw new NotFoundException("User not found");
        }
        log.info("Items of user: {}", userId);
        return itemService.getAllItemsOfUser(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(SHARER_USER_ID) Integer userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Add item: {}", itemDto);
        try {
            User user = UserMapper.toUser(userService.getUser(userId));
            log.info("Item added: {}", itemDto);
            return itemService.addItem(itemDto, user);
        } catch (Exception e) {
            throw new NotFoundException("User not found");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer itemId, @RequestBody ItemDto itemDto) {
        log.info("Update item: {}", itemDto);
        try {
            userService.getUser(userId);
        } catch (Exception e) {
            throw new NotFoundException("User not found");
        }
        log.info("Item updated: {}", itemDto);
        return itemService.updateItem(itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        log.info("Get item: {}", itemId);
        return itemService.getItem(itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Integer itemId) {
        log.info("Delete item: {}", itemId);
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Search items: {}", text);
        return itemService.searchItems(text);
    }
}
