package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ItemService {
    private int id = 1;
    Map<Integer, Item> items = new HashMap<>();
    @Autowired
    public ItemService() {
    }

    public Item addItem(ItemDto itemDto, int userId) {
        try {
            validateItem(itemDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        Item item = ItemMapper.toItem(itemDto);
        log.info("Adding item: {}", item);
        item.setId(id++);
        items.put(item.getId(), item);
        log.info("Item added: {}", item);
        return item;

    }
    public Map<Integer, Item> getAllItemsOfUser(int userId) {
        Map<Integer, Item> userItems = new HashMap<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                userItems.put(item.getId(), item);
            }
        }
        return userItems;
    }
    public Map<Integer, Item> getAllItems() {
        return items;
    }
    public Item updateItem(ItemDto itemDto, int id) {
        log.info("Updating item: {}", itemDto);
        if  (!items.containsKey(id)) {
            throw new IllegalArgumentException("Item not found");
        }
        try {
            validateUpdate(itemDto);
            Item item = items.get(id);
            if  (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if  (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if  (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            items.put(item.getId(), item);
            log.info("Item updated: {}", item);
            return item;
        } catch (ValidationException e){
            throw new ValidationException(e.getMessage());
        }

    }
    public Item getItem(int id) {
        return items.get(id);
    }
    public void deleteItem(int id) {
        items.remove(id);
    }
    public Map<Integer, Item> searchItem(String text) {
        Map<Integer, Item> foundItems = new HashMap<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.isAvailable()) {
                foundItems.put(item.getId(), item);
            }
        }
        return foundItems;
    }
    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null ) {
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
