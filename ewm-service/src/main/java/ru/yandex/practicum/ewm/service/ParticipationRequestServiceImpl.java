package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewm.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.yandex.practicum.ewm.exception.DuplicatedDataException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.exception.ValidationException;
import ru.yandex.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.ParticipationRequest;
import ru.yandex.practicum.ewm.model.RequestStatus;
import ru.yandex.practicum.ewm.model.State;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateInboundRequests(long userId, long eventId,
                                                                EventRequestStatusUpdateRequest request) {
        eventService.checkUser(userId);
        Event event = eventService.checkEvent(eventId);
        List<ParticipationRequest> requests = checkRequestIds(request.getRequestIds());
        requests.stream()
                .filter(r -> !r.getStatus().equals(RequestStatus.PENDING))
                .findFirst()
                .ifPresent(r -> {
                    //TODO 409 код
                    throw new DuplicatedDataException("Заявка с id=" + r.getId() +
                            " не в состоянии Ожидания подтверждения");
                });
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        if (event.getParticipantLimit() > 0 && event.isRequestModeration()) {
            RequestStatus requestStatus = request.getStatus();
            if (requestStatus.equals(RequestStatus.REJECTED)) {
                requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
                updateResult.setRejectedRequests(requests.stream()
                        .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                        .toList());
            }
            if (requestStatus.equals(RequestStatus.CONFIRMED)) {
                List<ParticipationRequest> confirmedRequests = new ArrayList<>();
                List<ParticipationRequest> rejectedRequests = new ArrayList<>();
                for (ParticipationRequest r : requests) {
                    if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                        r.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(r);
                    }
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(r);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
                if (!confirmedRequests.isEmpty()) {
                    updateResult.setConfirmedRequests(confirmedRequests.stream()
                            .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                            .toList());
                }
                if (!rejectedRequests.isEmpty()) {
                    updateResult.setRejectedRequests(rejectedRequests.stream()
                            .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                            .toList());
                    //TODO 409 код
                    throw new DuplicatedDataException("Достигнут лимит заявок на участие");
                }
            }
        }
        return updateResult;
    }

    @Override
    public Collection<ParticipationRequestDto> getUsersOutboundRequests(long userId) {
        eventService.checkUser(userId);
        return requestRepository.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public Collection<ParticipationRequestDto> getUsersInboundRequests(long userId, long eventId) {
        eventService.checkUser(userId);
        eventService.checkEvent(eventId);
        return requestRepository.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    private ParticipationRequest checkParticipationRequest(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос id=" + requestId + " не найден"));
    }

    private List<ParticipationRequest> checkRequestIds(List<Long> requestIds) {
        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);
        if (requests.isEmpty()) {
            throw new ValidationException("Запросы не найдены с ID: " + requestIds);
        } else {
            Set<Long> foundIds = requests.stream()
                    .map(ParticipationRequest::getId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = requestIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            if (!missingIds.isEmpty()) {
                throw new ValidationException("Запросы не найдены с ID: " + missingIds);
            }
        }
        return requests;
    }
}
