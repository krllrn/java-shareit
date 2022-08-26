package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(value = "select * from requests where request_owner_id = ?1 order by created desc", nativeQuery = true)
    List<ItemRequest> findByRequestOwnerIdContaining(Long userId);

    ItemRequest findByIdIs(Long requestId);

    @Query(value = "select * from requests where request_owner_id != ?1 order by created desc", nativeQuery = true)
    List<ItemRequest> findByRequestOwnerIdExcept(Long userId);
}
