package ru.yandex.practicum.ewm.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.ewm.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
public class CompilationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
