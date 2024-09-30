package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReceiver;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Autowired
    public ItemRequestService(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }
    public ItemRequestDto createItemRequest(Integer userId, ItemRequestReceiver itemRequestReceiver) {
        log.info("Creating item request");
        ItemRequest  itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestReceiver.getDescription());
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
            itemRequest.setRequester(user);
            itemRequestRepository.save(itemRequest);
            log.info("Item request created: {}", itemRequest);
            return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public Iterable<ItemRequestDto> getAllItemRequestsOfUser(Integer userId) {
        log.info("Getting all item requests of user {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).map(itemRequestDto -> {
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId()).stream().map(AnswerMapper::toAnwserDto).toList());
            return itemRequestDto;
        }).toList();
        log.info("Got all item requests of user {}", userId);
        return itemRequestDtos;
    }

    public Iterable<ItemRequestDto> getAllItemRequests(Integer userId) {
        log.info("Getting all item requests");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).map(itemRequestDto -> {
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId()).stream().map(AnswerMapper::toAnwserDto).toList());
            return itemRequestDto;
        }).collect(Collectors.toList());
        log.info("Got all item requests");
        return itemRequestDtos;
    }

    public ItemRequestDto getItemRequestById(Integer userId, Integer requestId) {
        log.info("Getting item request by id {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Item request not found"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<Answer> answers = itemRepository.findAllByRequestId(requestId);
        itemRequestDto.setItems(answers.stream().map(AnswerMapper::toAnwserDto).toList());
        log.info("Got item request by id {}", requestId);
        return itemRequestDto;
    }

}
