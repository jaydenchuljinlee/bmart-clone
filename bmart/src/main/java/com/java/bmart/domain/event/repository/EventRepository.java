package com.java.bmart.domain.event.repository;

import com.java.bmart.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByCreatedAtDesc();

    @Query("SELECT e FROM Event e "
            + "LEFT JOIN  FETCH e.eventItemList ei "
            + "WHERE e.eventId = :eventId")
    Optional<Event> findByIdWithEventItems(@Param("eventId") Long eventId);
}

