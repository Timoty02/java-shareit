package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;


import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader(SHARER_USER_ID)@Positive Integer userId) {
        log.info("Get all items of user: {}", userId);
        log.info("Items of user: {}", userId);
        return itemClient.getAllItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(SHARER_USER_ID) Integer userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Add item: {}", itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable @Positive Integer itemId, @RequestBody ItemDto itemDto) {
        log.info("Update item: {}", itemDto);
        log.info("Item updated: {}", itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable @Positive Integer itemId) {
        log.info("Get item: {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable @Positive Integer itemId) {
        log.info("Delete item: {}", itemId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Search items: {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable @Positive Integer itemId, @RequestBody @Valid CommentReceiver comment) {
        log.info("Add comment: {} from user with id:{} ", comment, userId);
        return itemClient.addComment(userId, itemId, comment);
    }
}
