package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReceiver;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private UserDto requester;
    private UserDto otherUser;
    private ItemRequestReceiver itemRequestReceiver;

    @BeforeEach
    void setUp() {
        // Create requester
        UserDto requesterUser = new UserDto();
        requesterUser.setName("Requester");
        requesterUser.setEmail("requester@example.com");
        requester = userService.addUser(requesterUser);

        // Create other user
        UserDto otherUserData = new UserDto();
        otherUserData.setName("Other User");
        otherUserData.setEmail("other@example.com");
        otherUser = userService.addUser(otherUserData);

        // Create item request receiver
        itemRequestReceiver = new ItemRequestReceiver();
        itemRequestReceiver.setDescription("Test Item Request");
    }

    @Test
    void testCreateAndRetrieveItemRequest() {
        // Create an item request
        ItemRequestDto createdRequest = itemRequestService.addItemRequest(requester.getId(), itemRequestReceiver);

        // Verify the item request was created
        assertNotEquals(0, createdRequest.getId());
        assertEquals(itemRequestReceiver.getDescription(), createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());

        // Retrieve the item request
        ItemRequestDto retrievedRequest = itemRequestService.getItemRequestById(requester.getId(), createdRequest.getId());

        // Verify the retrieved request matches the created request
        assertEquals(createdRequest.getId(), retrievedRequest.getId());
        assertEquals(createdRequest.getDescription(), retrievedRequest.getDescription());
        assertEquals(createdRequest.getCreated(), retrievedRequest.getCreated());
    }

    @Test
    void testGetAllItemRequestsOfUser() {
        // Create multiple item requests for the requester
        itemRequestService.addItemRequest(requester.getId(), itemRequestReceiver);
        itemRequestService.addItemRequest(requester.getId(), itemRequestReceiver);

        // Retrieve all item requests for the requester
        List<ItemRequestDto> requesterRequests = (List<ItemRequestDto>) itemRequestService.getAllItemRequestsOfUser(requester.getId());

        // Verify the number of requests
        assertEquals(2, requesterRequests.size());
    }

    @Test
    void testGetAllItemRequests() {
        // Create item requests for both users
        itemRequestService.addItemRequest(requester.getId(), itemRequestReceiver);
        itemRequestService.addItemRequest(otherUser.getId(), itemRequestReceiver);

        // Retrieve all item requests (excluding the requester's own requests)
        List<ItemRequestDto> allRequests = (List<ItemRequestDto>) itemRequestService.getAllItemRequests(requester.getId());

        // Verify the number of requests (should only include the other user's request)
        assertEquals(1, allRequests.size());
    }

    @Test
    void testGetItemRequestById() {
        // Create an item request
        ItemRequestDto createdRequest = itemRequestService.addItemRequest(requester.getId(), itemRequestReceiver);
        // Retrieve the item request by ID
        ItemRequestDto retrievedRequest = itemRequestService.getItemRequestById(requester.getId(), createdRequest.getId());
        // Verify the retrieved request
        assertEquals(createdRequest.getId(), retrievedRequest.getId());
        assertEquals(createdRequest.getDescription(), retrievedRequest.getDescription());
        assertEquals(createdRequest.getCreated(), retrievedRequest.getCreated());
    }

    @Test
    void testGetItemRequestByIdNotFound() {
        // Attempt to retrieve a non-existent item request
        ItemRequestDto createdRequest = itemRequestService.addItemRequest(requester.getId(), itemRequestReceiver);
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(requester.getId(), createdRequest.getId() + 1));
    }

    @Test
    void testCreateItemRequestWithInvalidUser() {
        // Attempt to create an item request with an invalid user ID
        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(requester.getId() + 99, itemRequestReceiver));
    }
}
