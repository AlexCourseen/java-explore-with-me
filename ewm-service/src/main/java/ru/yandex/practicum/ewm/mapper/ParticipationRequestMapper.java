package ru.yandex.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.yandex.practicum.ewm.model.ParticipationRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipationRequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest participationRequest) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(participationRequest.getId());
        dto.setRequester(participationRequest.getRequester().getId());
        dto.setEvent(participationRequest.getEvent().getId());
        dto.setStatus(participationRequest.getStatus());
        dto.setCreated(participationRequest.getCreated());
        return dto;
    }
}
