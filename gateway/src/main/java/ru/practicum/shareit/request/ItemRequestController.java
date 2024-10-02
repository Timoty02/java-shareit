package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(SHARER_USER_ID) Integer userId,
                                                    @RequestBody ItemRequestReceiver itemRequestReceiver) {
        log.info("Create item request: {}", itemRequestReceiver);
        return itemRequestClient.addItemRequest(userId, itemRequestReceiver);
    }

    @GetMapping("/{request-id}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable("request-id") @Positive Integer requestId) {
        log.info("Get item request: {}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsOfUser(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Get all item requests");
        return itemRequestClient.getAllItemRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Get all item requests with pagination");
        return itemRequestClient.getAllItemRequests(userId);
    }
}
