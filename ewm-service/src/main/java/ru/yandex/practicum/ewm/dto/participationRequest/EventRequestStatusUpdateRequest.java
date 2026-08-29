package ru.yandex.practicum.ewm.dto.participationRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.ewm.model.RequestStatus;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
