package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.dto.event.NewEventDto;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.LocationRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.ewm.model.State.PENDING;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с " + eventId + " не найдено"));
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public Collection<EventFullDto> getPublishedEvents(int from, int size, String sort, String text, List<Long> ids,
                                                       List<Long> catIds, Boolean paid, String start,
                                                       String end, Boolean onlyAvailable) {
        String search = (text != null && !text.isBlank()) ? text.toLowerCase() : null;
        LocalDateTime now = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null && !start.isBlank() && end != null && !end.isBlank()) {
            startTime = LocalDateTime.parse(start, formatter);
            endTime = LocalDateTime.parse(end, formatter);
            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
            }
        } else {
            now = LocalDateTime.now();
        }
        Sort sortBy = Sort.unsorted();
        if (sort != null) {
            if (sort.equalsIgnoreCase("EVENT_DATE")) {
                sortBy = Sort.by("eventDate").ascending();
            } else if (sort.equalsIgnoreCase("VIEWS")) {
                sortBy = Sort.by("views").descending();
            }
        }
        if (onlyAvailable) {
            return eventRepository.findEventsWithParamsOnlyAvailable(
                            (PageRequest.of(from, size, sortBy)), search, ids, catIds, paid,
                            startTime, endTime, now).stream()
                    .map(EventMapper::mapToEventFullDto)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findEventsWithParams(
                            (PageRequest.of(from, size, sortBy)), search, ids, catIds, paid,
                            endTime, endTime, now).stream()
                    .map(EventMapper::mapToEventFullDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto createEvent(long userId, NewEventDto request) {
        User initiator = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("user с " + userId + " не найден"));
        Category category = categoryRepository.findById(request.getCategory()).orElseThrow(() ->
                new NotFoundException("Категория c id=" + request.getCategory() + " не существует"));
        Event event = EventMapper.mapNewEventDtoToEvent(request, formatter);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        locationRepository.save(request.getLocation());
        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }
}