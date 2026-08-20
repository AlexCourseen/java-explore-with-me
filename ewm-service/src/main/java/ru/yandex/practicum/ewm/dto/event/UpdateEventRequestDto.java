package ru.yandex.practicum.ewm.dto.event;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.ewm.model.Location;
import ru.yandex.practicum.ewm.model.StateAction;

@Getter
@Setter
public class UpdateEventRequestDto {
    @Size(min = 20, max = 2000, message = "Краткое описание события должно быть от 20 до 2000 символов")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов")
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
    private StateAction stateAction;

    public boolean hasAnnotation() {
        return !(annotation == null || annotation.isBlank());
    }

    public boolean hasCategory() {
        return !(category == null);
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasEventDate() {
        return !(eventDate == null || eventDate.isBlank());
    }

    public boolean hasLocation() {
        return !(location == null);
    }

    public boolean hasPaid() {
        return !(paid == null);
    }

    public boolean hasParticipantLimit() {
        return !(participantLimit == null);
    }

    public boolean hasRequestModeration() {
        return !(requestModeration == null);
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }

    public boolean hasStateAction() {
        return !(stateAction == null);
    }
}
