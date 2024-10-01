package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentReceiver;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private UserDto testUser;
    private ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        // Create a test user
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        testUser = userService.addUser(userDto);

        // Create a test item DTO
        testItemDto = new ItemDto();
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);
    }

    @Test
    void testCreateAndRetrieveItem() {
        // Create an item
        ItemDto createdItem = itemService.addItem(testItemDto, testUser.getId());

        // Verify the item was created
        assertNotNull(createdItem.getId());
        assertEquals(testItemDto.getName(), createdItem.getName());
        assertEquals(testItemDto.getDescription(), createdItem.getDescription());
        assertEquals(testItemDto.getAvailable(), createdItem.getAvailable());

        // Retrieve the item
        ItemDto retrievedItem = itemService.getItem(createdItem.getId(), testUser.getId());

        // Verify the retrieved item matches the created item
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals(createdItem.getName(), retrievedItem.getName());
        assertEquals(createdItem.getDescription(), retrievedItem.getDescription());
        assertEquals(createdItem.getAvailable(), retrievedItem.getAvailable());
    }

    @Test
    void testUpdateItem() {
        // Create an item
        ItemDto createdItem = itemService.addItem(testItemDto, testUser.getId());

        // Update the item
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Item");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);
        ItemDto updatedDto = itemService.updateItem(updateDto, createdItem.getId(), testUser.getId());


        // Verify the item was updated
        assertEquals(createdItem.getId(), updatedDto.getId());
        assertEquals("Updated Item", updatedDto.getName());
        assertEquals("Updated Description", updatedDto.getDescription());
        assertEquals(false, updatedDto.getAvailable());
    }

    @Test
    void testDeleteItem() {
        // Create an item
        ItemDto createdItem = itemService.addItem(testItemDto, testUser.getId());

        // Delete the item
        itemService.deleteItem(createdItem.getId());

        // Verify the item was deleted
        assertThrows(NotFoundException.class, () -> itemService.getItem(testUser.getId(), createdItem.getId()));
    }

    @Test
    void testGetAllUserItems() {
        // Create multiple items for the user
        itemService.addItem(testItemDto, testUser.getId());
        itemService.addItem(testItemDto, testUser.getId());

        // Retrieve all items for the user
        List<ItemDto> userItems = itemService.getAllItemsOfUser(testUser.getId());

        // Verify the number of items
        assertEquals(2, userItems.size());
    }

    @Test
    void testSearchItems() {
        // Create items with specific names
        ItemDto item1 = new ItemDto();
        item1.setName("Searchable Item");
        item1.setDescription("This is a searchable item");
        item1.setAvailable(true);
        itemService.addItem(item1, testUser.getId());

        ItemDto item2 = new ItemDto();
        item2.setName("Another Item");
        item2.setDescription("This is another item");
        item2.setAvailable(true);
        itemService.addItem(item2, testUser.getId());

        // Search for items
        List<ItemDto> searchResults = itemService.searchItems("searchable");
        // Verify search results
        assertEquals(1, searchResults.size());
        assertEquals("Searchable Item", searchResults.get(0).getName());
    }

    @Test
    void testAddCommentToItem() {
        // Create an item
        ItemDto createdItem = itemService.addItem(testItemDto, testUser.getId());


        // Add a comment to the item
        CommentReceiver commentReceiver = new CommentReceiver();
        commentReceiver.setText("This is a test comment");
        assertThrows(ValidationException.class, () -> itemService.addComment(testUser.getId(), createdItem.getId(), commentReceiver));

        // Verify the comment was added
    }
}
