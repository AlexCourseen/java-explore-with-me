package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    ParticipationRequest findByRequesterIdAndEventId(long requesterId, long eventId);

    List<ParticipationRequest> findByRequesterId(long requesterId);

    List<ParticipationRequest> findByEventId(long eventId);
}
