package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public ItemDto addItem(ItemDto itemDto, User user) {
        log.info("Adding item: {}", itemDto);
        try {
            validateItem(itemDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item item1 = repository.save(item);
        log.info("Item added: {}", item1);
        return ItemMapper.toItemDto(item1);
    }

    public List<ItemDto> getAllItemsOfUser(int userId) {
        List<Item> userItems = repository.findAllByOwner(userId);
        return userItems.stream().map(ItemMapper::toItemDto).toList();
    }

    public ItemDto updateItem(ItemDto itemDto, int id) {
        log.info("Updating item: {}", itemDto);
        try {
            validateUpdate(itemDto);
            Optional<Item> itemOptional = repository.findById(id);
            Item item = itemOptional.orElseThrow(() -> new NotFoundException("Item not found"));
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            Item itemUp = repository.save(item);
            log.info("Item updated: {}", itemUp);
            return ItemMapper.toItemDto(itemUp);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

    }

    public ItemDto getItem(int id) {
        log.info("Getting item with id: {}", id);
        Optional<Item> itemOptional = repository.findById(id);
        Item item = itemOptional.orElseThrow(() -> new NotFoundException("Item not found"));
        log.info("Item found: {}", item);
        return ItemMapper.toItemDto(item);
    }

    public void deleteItem(int id) {
        log.info("Deleting item with id: {}", id);
        repository.deleteById(id);
        log.info("Item deleted");
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = repository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
        return items.stream().map(ItemMapper::toItemDto).toList();
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
