package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RepositoryRestResource
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * from items where owner_id = ?1 order by id asc", nativeQuery = true)
    List<Item> findByUserIdContaining(Long userId);

    @Query(value = "select * from items where request_id = ?1 order by id asc", nativeQuery = true)
    List<Item> findByRequestIdContaining(Long requestId);

    @Query(value = "select * from items where id = ?1 order by id asc", nativeQuery = true)
    Item findByIdIs(Long itemId);
}
