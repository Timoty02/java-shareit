package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReceiver;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemService(ItemRepository repository, CommentRepository commentRepository, UserRepository userRepository, BookingRepository bookingRepository, ItemRequestRepository itemRequestRepository) {
        this.itemRepository = repository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public ItemDto addItem(ItemDto itemDto, int userId) {
        log.info("Adding item: {}", itemDto);
        try {
            validateItem(itemDto);
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(user);
            if (itemDto.getRequestId()!=null) {item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Request not found")));}
            Item item1 = itemRepository.save(item);
            log.info("Item added: {}", item1);
            return ItemMapper.toItemDto(item1);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public List<ItemDto> getAllItemsOfUser(int userId) {
        List<Item> userItems = itemRepository.findAllByOwner(userId);
        return userItems.stream().map(ItemMapper::toItemDto).toList();
    }

    public ItemDto updateItem(ItemDto itemDto, int id, int userId) {
        log.info("Updating item: {}", itemDto);
        try {
            validateUpdate(itemDto);
            Optional<Item> itemOptional = itemRepository.findById(id);
            Item item = itemOptional.orElseThrow(() -> new NotFoundException("Item not found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
            if (item.getOwner().getId() != user.getId()) {
                throw new NotFoundException("User is not the owner of the item");
            }
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            Item itemUp = itemRepository.save(item);
            log.info("Item updated: {}", itemUp);
            return ItemMapper.toItemDto(itemUp);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

    }

    public ItemDto getItem(int id, int userId) {
        log.info("Getting item with id: {}", id);
        Optional<Item> itemOptional = itemRepository.findById(id);
        Item item = itemOptional.orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        log.info("Comments: {}", comments);
        if  (comments.isEmpty()) {
            itemDto.setComments(new ArrayList<>());
        } else {
            itemDto.setComments(commentRepository.findAllByItemId(id).stream().map(comment -> {
                CommentDto commentDto = new CommentDto();
                commentDto.setId(comment.getId());
                commentDto.setText(comment.getText());
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentDto.setCreated(comment.getCreated());
                return commentDto;
            }).toList());
        }
        if (item.getOwner().getId() == userId) {
            try {
                Booking booking1 = bookingRepository.findLastBookingByItemId(id).orElseThrow(() -> new NotFoundException("Booking not found"));
                itemDto.setLastBooking(booking1.getEnd());
            } catch (NotFoundException e) {
                itemDto.setLastBooking(null);
            }
            try {
                Booking booking2 = bookingRepository.findNextBookingByItemId(id).orElseThrow(() -> new NotFoundException("Booking not found"));
                itemDto.setNextBooking(booking2.getStart());
            } catch (NotFoundException e) {
                itemDto.setNextBooking(null);
            }
        }
        log.info("Item found: {}", itemDto);
        return itemDto;
    }

    public void deleteItem(int id) {
        log.info("Deleting item with id: {}", id);
        itemRepository.deleteById(id);
        log.info("Item deleted");
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public CommentDto addComment(int userId, int itemId, CommentReceiver commentText) {
        log.info("Adding comment: {}", commentText);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        Item item = itemOptional.orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (bookingRepository.findAllByBookerAndItemAndStatus(userId, itemId, BookingStatus.APPROVED.name()).isEmpty()) {
            throw new ValidationException("User has not booked this item");
        }
        Comment comment = new Comment();
        comment.setText(commentText.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        Comment commentAdd = commentRepository.save(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentAdd.getId());
        commentDto.setText(commentAdd.getText());
        commentDto.setAuthorName(commentAdd.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        log.info("Comment added: {}", commentDto);
        return commentDto;
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
