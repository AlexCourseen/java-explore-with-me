package ru.yandex.practicum.ewm.dto.participationRequest;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.ewm.model.RequestStatus;

import java.util.Set;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private Set<Long> event;
    private RequestStatus status;
}
