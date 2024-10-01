package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReceiver;
import ru.practicum.shareit.item.dto.ItemDto;

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
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Get all items of user: {}", userId);
        log.info("Items of user: {}", userId);
        return itemService.getAllItemsOfUser(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(SHARER_USER_ID) Integer userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Add item: {}", itemDto);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer itemId, @RequestBody ItemDto itemDto) {
        log.info("Update item: {}", itemDto);
        log.info("Item updated: {}", itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("Get item: {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("Delete item: {}", itemId);
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Search items: {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer itemId, @RequestBody @Valid CommentReceiver comment) {
        log.info("Add comment: {} from user with id:{} ", comment, userId);
        return itemService.addComment(userId, itemId, comment);
    }
}
