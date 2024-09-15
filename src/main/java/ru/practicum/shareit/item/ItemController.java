package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    private final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public Map<Integer, Item> getAllItemsOfUser (@RequestHeader(SHARER_USER_ID)Integer userId) {
        try {
            userService.getUser(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found");
        }
        return itemService.getAllItemsOfUser(userId);
    }
    @PostMapping
    public Item addItem(@RequestHeader(SHARER_USER_ID)Integer userId, @RequestBody ItemDto itemDto) {
        try {
            userService.getUser(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found");
        }
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(SHARER_USER_ID)Integer userId, @PathVariable Integer itemId, @RequestBody ItemDto itemDto) {
        try {
            userService.getUser(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found");
        }
        return itemService.updateItem(itemDto, itemId);
    }
}
