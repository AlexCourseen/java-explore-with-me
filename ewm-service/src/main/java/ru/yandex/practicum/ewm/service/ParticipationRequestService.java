package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.participationRequest.ParticipationRequestDto;

public interface ParticipationRequestService {
    ParticipationRequestDto createOutboundRequest(long userId, long eventId);

    ParticipationRequestDto cancelOutboundRequest(long userId, long requestId);

//    Collection<ParticipationRequestDto> getUsersOutboundRequests(long userId, long eventId);
//
//    Collection<ParticipationRequestDto> getUsersInboundRequests(long userId, long eventId);
//
//    EventRequestStatusUpdateResult updateInboundRequests(long userId, long eventId,
//                                                         EventRequestStatusUpdateRequest request);

}
