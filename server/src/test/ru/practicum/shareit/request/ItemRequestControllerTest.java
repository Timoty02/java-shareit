package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestReceiver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequestReceiver  itemRequestReceiver;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Test Item Request");
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestReceiver = new ItemRequestReceiver();
        itemRequestReceiver.setDescription("Test Item Request");
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(anyInt(), any(ItemRequestReceiver.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestReceiver)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void testGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void testGetAllItemRequestsOfUser() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        when(itemRequestService.getAllItemRequestsOfUser(anyInt())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    void testGetAllItemRequests() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        when(itemRequestService.getAllItemRequests(anyInt())).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    void testGetItemRequestByIdNotFound() throws Exception {
        when(itemRequestService.getItemRequestById(anyInt(), anyInt())).thenThrow(new NotFoundException("Item request not found"));
        mockMvc.perform(get("/requests/1")
                .header(USER_ID_HEADER, "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateItemRequestWithInvalidUser() throws Exception {
        when(itemRequestService.addItemRequest(anyInt(), any(ItemRequestReceiver.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());
    }


    @Test
    void testGetAllItemRequestsEmpty() throws Exception {
        when(itemRequestService.getAllItemRequests(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
