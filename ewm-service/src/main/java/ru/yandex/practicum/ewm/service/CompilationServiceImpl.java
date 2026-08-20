package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.dto.event.EventShortDto;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.exception.ValidationException;
import ru.yandex.practicum.ewm.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CompilationRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto request) {
        Compilation compilation = new Compilation();
        List<EventShortDto> events = new ArrayList<>();
        if (!request.getEvents().isEmpty()) {
            events = checkEventIds(request.getEvents());
        }
        compilation.setTitle(request.getTitle());
        compilation.setPinned(request.isPinned());
        compilationRepository.save(compilation);
        CompilationDto compDto = CompilationMapper.mapToCompilationDto(compilation);
        compDto.setEvents(events);
        return compDto;
    }

    @Override
    public void delCompilation(long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest request) {
        Compilation compilation = checkCompilation(compId);
        CompilationMapper.updateCompilation(request, compilation);
        CompilationDto compDto = CompilationMapper.mapToCompilationDto(compilation);
        if (request.hasEvents()) {
            compDto.setEvents(checkEventIds(request.getEvents()));
        }
        return compDto;
    }

    private Compilation checkCompilation(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с " + compId + " не найдено"));
    }

    private List<EventShortDto> checkEventIds(List<Long> eventIds) {
        List<Event> events = eventRepository.findAllById(eventIds);
        if (events.isEmpty()) {
            throw new ValidationException("События не найдены с ID: " + eventIds);
        } else {
            Set<Long> foundIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = eventIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            if (!missingIds.isEmpty()) {
                throw new ValidationException("События не найдены с ID: " + missingIds);
            }
        }
        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }
}
