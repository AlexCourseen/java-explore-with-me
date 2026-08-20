package ru.yandex.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50, message = "Название подборки должно быть от 1 до 50 символов")
    private String title;
    private Boolean pinned;
    private List<Long> events;

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }

    public boolean hasPinned() {
        return !(pinned == null);
    }

    public boolean hasEvents() {
        return !(events == null || events.isEmpty());

    }
}
