package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.exception.ValidationException;
import ru.yandex.practicum.ewm.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CompilationRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto request) {
        Compilation compilation = new Compilation();
        if (!request.getEvents().isEmpty()) {
            compilation.setEvents(checkEventIds(request.getEvents()));
        }
        compilation.setTitle(request.getTitle());
        compilation.setPinned(request.isPinned());
        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.mapToCompilationDto(savedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(int from, int size, Boolean pinned) {
        return compilationRepository.findCompWithParams(PageRequest.of(from, size), pinned)
                .stream()
                .map(CompilationMapper::mapToCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = checkCompilation(compId);
        return CompilationMapper.mapToCompilationDto(compilation);
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
        if (request.hasEvents()) {
            compilation.setEvents(checkEventIds(request.getEvents()));
        }
        CompilationMapper.updateCompilation(request, compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    private Compilation checkCompilation(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с " + compId + " не найдено"));
    }

    private List<Event> checkEventIds(List<Long> eventIds) {
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
        return events;
    }
}
