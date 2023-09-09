package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where  i.available = true  and "
            + "(lower(i.name) like lower(:text) or lower(i.description) like lower(:text))")
    List<Item> findByNameOrDescription(@Nullable String text);

    Page<Item> findByOwnerId(long ownerId, Pageable pageable);

    List<Item> findByRequestIdOrderById(Long requestId);
}