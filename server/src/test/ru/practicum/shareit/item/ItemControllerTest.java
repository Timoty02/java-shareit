package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReceiver;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private CommentDto commentDto;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Test Comment");
        commentDto.setAuthorName("Test Author");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        when(itemService.updateItem(any(ItemDto.class), anyInt(),anyInt())).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(updatedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(updatedItemDto.getAvailable()));
    }

    @Test
    void testGetItem() throws Exception {
        when(itemService.getItem(anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void testGetAllItems() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.getAllItemsOfUser(anyInt())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void testSearchItems() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.searchItems(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentReceiver.class))).thenReturn(commentDto);
        CommentReceiver commentReceiver = new CommentReceiver();
        commentReceiver.setText(commentDto.getText());
        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentReceiver)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").exists());
    }
    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk());
    }

}
