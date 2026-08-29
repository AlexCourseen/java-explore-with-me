package ru.yandex.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.ewm.dto.category.CategoryDto;
import ru.yandex.practicum.ewm.dto.location.LocationDto;
import ru.yandex.practicum.ewm.dto.user.UserShortDto;
import ru.yandex.practicum.ewm.model.State;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFullDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private long views;
}
