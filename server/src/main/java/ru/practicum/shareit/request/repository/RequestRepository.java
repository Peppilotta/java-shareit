package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    @Query("select r from Request r where r.requester.id <> :id order by r.created desc")
    Page<Request> getAllCreatedByOtherOrderByCreatedDesc(Long id, Pageable page);

    List<Request> getAllByRequesterIdOrderByCreatedDesc(Long requesterId);
}