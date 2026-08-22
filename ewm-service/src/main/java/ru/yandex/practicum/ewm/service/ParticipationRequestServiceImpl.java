package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.yandex.practicum.ewm.exception.DuplicatedDataException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.ParticipationRequest;
import ru.yandex.practicum.ewm.model.RequestStatus;
import ru.yandex.practicum.ewm.model.State;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventServiceImpl eventService;

    @Override
    public ParticipationRequestDto createOutboundRequest(long userId, long eventId) {
        Event event = eventService.checkEvent(eventId);
        User requester = eventService.checkUser(userId);
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            throw new DuplicatedDataException("Повторный запрос на участие невозможен");
        }
        if (event.getInitiator().equals(requester)) {
            //TODO 409 код
            throw new DuplicatedDataException("Невозможно создать запрос на участие в своем событие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            //TODO 409 код
            throw new DuplicatedDataException("Невозможно участвовать в неопубликованном событие");
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            //TODO 409 код
            throw new DuplicatedDataException("Достигнут лимит запросов на участие");
        }
        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        requestRepository.save(request);
        return ParticipationRequestMapper.mapToParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelOutboundRequest(long userId, long requestId) {
        ParticipationRequest participationRequest = checkParticipationRequest(requestId);
        participationRequest.setStatus(RequestStatus.REJECTED);
        return ParticipationRequestMapper.mapToParticipationRequestDto(participationRequest);
    }

    //
//    Collection<ParticipationRequestDto> getUsersOutboundRequests(long userId, long eventId);
//
//    Collection<ParticipationRequestDto> getUsersInboundRequests(long userId, long eventId);
//
//    EventRequestStatusUpdateResult updateInboundRequests(long userId, long eventId,
//                                                         EventRequestStatusUpdateRequest request);
    private ParticipationRequest checkParticipationRequest(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос id=" + requestId + " не найден"));
    }
}
