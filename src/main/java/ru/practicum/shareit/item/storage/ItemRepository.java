package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long id);

    @Query("select i from Item i where  i.available = true  and "
            + "(lower(i.name) like lower(?1) or lower(i.description) like lower(?1))")
    List<Item> findByNameOrDescription(@Nullable String text);

    @Query("select i from Item i where i.id = ?1")
    Optional<ItemShortAvailability> isItemAvailable(Long Long);
}