package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Answer;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query(value = "SELECT * FROM items WHERE owner_id = ?1", nativeQuery = true)
    List<Item> findAllByOwner(int userId);

    @Query(value = "SELECT * FROM items WHERE (name ILIKE %?1% OR description ILIKE %?2%) and is_available = true", nativeQuery = true)
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String text, String text1);
    @Query("select i from Item i JOIN FETCH i.owner as o where i.request.id = ?1")
    List<Answer> findAllByRequestId(int requestId);
    @Query(value = "SELECT * FROM items WHERE request_id IS NOT NULL", nativeQuery = true)
    List<Answer> findAllbyRequestIdNotNull();
}
