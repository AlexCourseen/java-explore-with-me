package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (CAST(:text AS string) IS NULL OR " +
            "LOWER(e.annotation) LIKE CONCAT('%', CAST(:text AS string), '%') OR " +
            "LOWER(e.description) LIKE CONCAT('%', CAST(:text AS string), '%')) " +
            "AND (:catIds IS NULL OR e.category.id IN :catIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:now AS timestamp) IS NULL OR e.eventDate > :now) " +
            "AND (CAST(:start AS timestamp) IS NULL OR e.eventDate >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR e.eventDate <= :end) " +
            "AND e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit ")
    List<Event> findEventsWithParamsOnlyAvailable(Pageable pageable,
                                                  @Param("text") String text,
                                                  @Param("catIds") List<Long> catIds,
                                                  @Param("paid") Boolean paid,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (CAST(:text AS string) IS NULL OR " +
            "LOWER(e.annotation) LIKE CONCAT('%', CAST(:text AS string), '%') OR " +
            "LOWER(e.description) LIKE CONCAT('%', CAST(:text AS string), '%')) " +
            "AND (:catIds IS NULL OR e.category.id IN :catIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:now AS timestamp) IS NULL OR e.eventDate > :now) " +
            "AND (CAST(:start AS timestamp) IS NULL OR e.eventDate >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR e.eventDate <= :end)")
    List<Event> findEventsWithParams(Pageable pageable,
                                     @Param("text") String text,
                                     @Param("catIds") List<Long> catIds,
                                     @Param("paid") Boolean paid,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e " +
            "WHERE (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> findEventsByAdmin(Pageable pageable,
                                  @Param("states") List<State> states,
                                  @Param("users") List<Long> users,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd);
}
