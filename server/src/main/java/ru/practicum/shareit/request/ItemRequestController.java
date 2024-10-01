package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReceiver;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    ItemRequestService itemRequestService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(SHARER_USER_ID) Integer userId,
                                            @RequestBody ItemRequestReceiver itemRequestReceiver) {
        log.info("Create item request: {}", itemRequestReceiver);
        return itemRequestService.addItemRequest(userId, itemRequestReceiver);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(SHARER_USER_ID) Integer userId, @PathVariable Integer requestId) {
        log.info("Get item request: {}", requestId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
    @GetMapping
    public Iterable<ItemRequestDto> getAllItemRequestsOfUser(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Get all item requests");
        return itemRequestService.getAllItemRequestsOfUser(userId);
    }
    @GetMapping("/all")
    public Iterable<ItemRequestDto> getAllItemRequests(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Get all item requests with pagination");
        return itemRequestService.getAllItemRequests(userId);
    }
}
