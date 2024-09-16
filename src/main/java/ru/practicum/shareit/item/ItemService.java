package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ItemService {
    private int id = 1;
    Map<Integer, Item> items = new HashMap<>();

    @Autowired
    public ItemService() {
    }

    public ItemDto addItem(ItemDto itemDto, User user) {
        log.info("Adding item: {}", itemDto);
        try {
            validateItem(itemDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setId(id++);
        item.setOwner(user);
        items.put(item.getId(), item);
        log.info("Item added: {}", item);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAllItemsOfUser(int userId) {
        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    public ItemDto updateItem(ItemDto itemDto, int id) {
        log.info("Updating item: {}", itemDto);
        if (!items.containsKey(id)) {
            throw new IllegalArgumentException("Item not found");
        }
        try {
            validateUpdate(itemDto);
            Item item = items.get(id);
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            items.put(item.getId(), item);
            log.info("Item updated: {}", item);
            return ItemMapper.toItemDto(item);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

    }

    public ItemDto getItem(int id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    public void deleteItem(int id) {
        items.remove(id);
    }

    public List<ItemDto> searchItems(String text) {
        List<ItemDto> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable()) {
                foundItems.add(ItemMapper.toItemDto(item));
            }
        }
        return foundItems;
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Validation exception");
        }
    }

    private void validateUpdate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank() ||
                itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new ValidationException("Validation exception");
        }
    }
}
