package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findByIdIs(Long bookingId);

    //----------------ITEM---------------------------------------------------

    @Query(value = "select distinct on (item_id, end_date) * from bookings where item_id = ?1 and end_date < ?2 " +
            "order by end_date desc", nativeQuery = true)
    Booking findByItemIdAndEndDate(Long itemId, LocalDateTime localDateTime);

    @Query(value = "select distinct on (item_id, start_date) * from bookings where item_id = ?1 and start_date > ?2 " +
            "order by end_date desc", nativeQuery = true)
    Booking findByItemIdAndStartDate(Long itemId, LocalDateTime localDateTime);

    //------------------OWNER----------------------------------------------

    @Query(value = "select * from bookings where item_owner_id = ?1 order by start_date desc", nativeQuery = true)
    List<Booking> findByItemOwnerId(Long ownerId, Pageable page);

    @Query(value = "select * from bookings where item_owner_id = ?1 and status like ?2% order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingState status, Pageable page);

    @Query(value = "select * from bookings where item_owner_id = ?1 and start_date <= ?2 and end_date >= ?2 " +
            "order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerIdAndStartAfterAndEndBefore(Long ownerId, LocalDateTime dateTime, Pageable page);

    @Query(value = "select * from bookings where item_owner_id = ?1 and end_date < ?2 order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerIdInPast(Long ownerId, LocalDateTime dateTime, Pageable page);

    @Query(value = "select * from bookings where item_owner_id = ?1 and start_date > ?2 order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerIdInFuture(Long ownerId, LocalDateTime dateTime, Pageable page);

    //------------------------BOOKER_ID---------------------------------------

    @Query(value = "select * from bookings where booker_id = ?1 order by start_date desc", nativeQuery = true)
    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query(value = "select * from bookings where booker_id = ?1 and status like ?2% order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingState status, Pageable page);

    @Query(value = "select * from bookings where booker_id = ?1 and start_date <= ?2 and end_date >= ?2 " +
            "order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByBookerIdAndStartAfterAndEndBefore(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select * from bookings where booker_id = ?1 and end_date < ?2 order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByBookerIdInPast(Long bookerId, LocalDateTime dateTime, Pageable page);

    @Query(value = "select * from bookings where booker_id = ?1 and start_date > ?2 order by start_date desc",
            nativeQuery = true)
    List<Booking> findAllByBookerIdInFuture(Long bookerId, LocalDateTime dateTime, Pageable page);

    @Query(value = "select * from bookings where booker_id = ?1 and item_id = ?2 and " +
            "(end_date < ?3 and status not like ?4%) order by start_date desc",
            nativeQuery = true)
    Booking findByBookerIdAndItemIdAndStartDateCorrectOrStatus(Long bookerId, Long itemId, LocalDateTime dateTime, BookingState state);
}
