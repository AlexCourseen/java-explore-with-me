package ru.yandex.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.model.Compilation;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static Compilation mapNewCompilationDtoToCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.isPinned());
        return compilation;
    }

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        dto.setId(compilation.getId());

        if (compilation.getEvents() != null) {
            dto.setEvents(compilation.getEvents().stream()
                    .map(EventMapper::mapToEventShortDto)
                    .toList());
        } else {
            dto.setEvents(List.of());
        }
        return dto;
    }

    public static void updateCompilation(UpdateCompilationRequest request, Compilation compilation) {
        if (request.hasPinned()) {
            compilation.setPinned(request.getPinned());
        }
        if (request.hasTitle()) {
            compilation.setTitle(request.getTitle());
        }
    }
}
