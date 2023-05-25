package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> searchByBookerAndStatus(@NonNull Long id, @NonNull BookingStatus status);

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC")
    List<Booking> searchByBooker(@NonNull Long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> searchByBookerInPresentTime(Long bookerId, LocalDateTime date);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> searchByBookerInPastTime(Long bookerId, LocalDateTime date);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> searchByBookerInFutureTime(Long bookerId, LocalDateTime date);

    @Query("select b from Booking b where   b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> searchByItemOwner(@NonNull Long id);

    @Query("select b from Booking b where  b.item.owner.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> searchByItemOwnerInPastTime(@NonNull Long id, LocalDateTime date);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start >?2 and b.end > ?2  order by b.start DESC")
    List<Booking> searchBookingsByItemOwnerInFutureTime(@NonNull Long id, LocalDateTime date);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start <?2 and b.end > ?2  order by b.start DESC")
    List<Booking> searchByItemOwnerInPresentTime(@NonNull Long id, LocalDateTime date);

    @Query("select b from Booking b where b.id = ?1 and b.booker.id = ?2 order by b.start DESC")
    List<Booking> searchById(@NonNull Long itemId, @NonNull Long bookerId);

    @Query("select b from Booking b where b.item.id = ?1 and ((b.start < ?3 and b.end >?3)" +
            " or (b.start < ?2 and b.end >?2) or (b.start > ?2 and b.end <?3)) order by b.start DESC")
    List<Booking> searchByItemIdAndStartAddEnd(@NonNull Long itemId, LocalDateTime start, LocalDateTime end);

    @Query("select b from Booking b where b.item.id = ?1 and b.end <?2 order by b.end DESC")
    List<Booking> searchByItemIdAndEndBeforeDate(@NonNull Long itemId, LocalDateTime date);

    @Query("select b from Booking b where b.item.id = ?1 and b.start >?2 order by b.start ASC")
    List<Booking> searchByItemIdAndStartAfterDate(@NonNull Long itemId, LocalDateTime date);

    boolean existsByItemIdAndBookerIdAndStatusAndEndIsBefore(@NonNull Long itemId,

                                                             @NonNull Long bookerId,
                                                             @NonNull BookingStatus status,
                                                             LocalDateTime end);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = ?2 " +
            "order by b.start DESC")
    List<Booking> searchByItemOwnerAndStatus(@NonNull Long id,
                                             @NonNull BookingStatus status);
}