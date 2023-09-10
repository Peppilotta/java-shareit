package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("select b from Booking b where b.booker.id = :id and b.status = :status order by b.start DESC")
    Page<Booking> searchByBookerAndStatus(@NonNull Long id, @NonNull BookingStatus status, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :id order by b.start DESC")
    Page<Booking> searchByBooker(@NonNull Long id, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.start < :date and b.end > :date order by b.start DESC")
    Page<Booking> searchByBookerInPresentTime(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.end < :date order by b.start DESC")
    Page<Booking> searchByBookerInPastTime(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.start > :date order by b.start DESC")
    Page<Booking> searchByBookerInFutureTime(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :id order by b.start DESC")
    Page<Booking> searchByItemOwner(@NonNull Long id, Pageable pageable);

    @Query("select b from Booking b where  b.item.owner.id = :id and b.end < :date order by b.start DESC")
    Page<Booking> searchByItemOwnerInPastTime(@NonNull Long id, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :id and b.start > :date and b.end > :date  order by b.start DESC")
    Page<Booking> searchBookingsByItemOwnerInFutureTime(@NonNull Long id, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :id and b.start < :date and b.end > :date  order by b.start DESC")
    Page<Booking> searchByItemOwnerInPresentTime(@NonNull Long id, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.item.id = :itemId and ((b.start < :end and b.end > :end)" +
            " or (b.start < :start and b.end > :start) or (b.start > :start and b.end < :end)) order by b.start desc")
    List<Booking> searchByItemIdAndStartAddEnd(@NonNull Long itemId, LocalDateTime start, LocalDateTime end);

    @Query("select b from Booking b where b.item.id = :itemId and b.start < :date and b.status = :status order by b.start DESC")
    List<Booking> searchByItemIdAndEndBeforeDate(@NonNull Long itemId, LocalDateTime date, BookingStatus status);

    @Query("select b from Booking b where b.item.id = :itemId and b.start > :date and b.status = :status order by b.start ASC")
    List<Booking> searchByItemIdAndStartAfterDate(@NonNull Long itemId, LocalDateTime date, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndIsBefore(@NonNull Long itemId,

                                                             @NonNull Long bookerId,
                                                             @NonNull BookingStatus status,
                                                             LocalDateTime end);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :id and b.status = :status " +
            "order by b.start DESC")
    Page<Booking> searchByItemOwnerAndStatus(@NonNull Long id,
                                             @NonNull BookingStatus status, Pageable pageable);
}