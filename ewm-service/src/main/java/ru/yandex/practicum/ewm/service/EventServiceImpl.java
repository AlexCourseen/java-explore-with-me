package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.dto.event.EventShortDto;
import ru.yandex.practicum.ewm.dto.event.NewEventDto;
import ru.yandex.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.ewm.dto.event.UpdateEventRequestDto;
import ru.yandex.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.ewm.exception.ConflictedDataException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.exception.ValidationException;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.Location;
import ru.yandex.practicum.ewm.model.State;
import ru.yandex.practicum.ewm.model.StateAction;
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

import static ru.yandex.practicum.ewm.model.State.CANCELED;
import static ru.yandex.practicum.ewm.model.State.PENDING;
import static ru.yandex.practicum.ewm.model.State.PUBLISHED;
import static ru.yandex.practicum.ewm.model.StateAction.CANCEL_REVIEW;
import static ru.yandex.practicum.ewm.model.StateAction.REJECT_EVENT;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public EventFullDto getEvent(long eventId) {
        Event event = checkEvent(eventId);
        if (!isEventPublished(event)) {
            throw new NotFoundException("Событие с " + eventId + " не найдено");
        }
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public Collection<EventShortDto> getPublishedEvents(int from, int size, String sort, String text, List<Long> catIds,
                                                        Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable) {
        String search = (text != null && !text.isBlank()) ? text.toLowerCase() : null;
        LocalDateTime now = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (rangeStart != null && !rangeStart.isBlank() && rangeEnd != null && !rangeEnd.isBlank()) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
            endTime = LocalDateTime.parse(rangeEnd, formatter);
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
                            (PageRequest.of(from, size, sortBy)), search, catIds, paid,
                            startTime, endTime, now).stream()
                    .map(EventMapper::mapToEventShortDto)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findEventsWithParams(
                            (PageRequest.of(from, size, sortBy)), search, catIds, paid,
                            endTime, endTime, now).stream()
                    .map(EventMapper::mapToEventShortDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Collection<EventFullDto> getEventsByAdmin(int from,
                                                     int size,
                                                     List<State> states,
                                                     List<Long> users,
                                                     List<Long> categories,
                                                     String rangeStart,
                                                     String rangeEnd) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (rangeStart != null && !rangeStart.isBlank()) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd != null && !rangeEnd.isBlank()) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
            }
        }
        return eventRepository.findEventsByAdmin(PageRequest.of(from, size), states, users, categories, startTime,
                        endTime)
                .stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());

    }

    @Override
    public EventFullDto createEvent(long userId, NewEventDto request) {
        User initiator = checkUser(userId);
        Category category = categoryRepository.findById(request.getCategory()).orElseThrow(() ->
                new NotFoundException("Категория c id=" + request.getCategory() + " не найдена"));
        LocalDateTime eventDate = LocalDateTime.parse(request.getEventDate(), formatter);
        checkEventDate(eventDate);
        Location location = checkLocation(request.getLocation());
        if (request.getRequestModeration() == null) {
            request.setRequestModeration(true);
        }
        Event event = EventMapper.mapNewEventDtoToEvent(request);
        event.setEventDate(eventDate);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        event.setLocation(location);
        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request) {
        Event event = checkEvent(eventId);
        updateEventFields(event, request);
        if (request.hasStateAction()) {
            StateAction stateAction = request.getStateAction();
            if (isEventPublished(event)) {
                throw new ConflictedDataException("Невозможно изменить событие в статусе PUBLISHED");
            }
            if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
                if (event.getState().equals(CANCELED)) {
                    throw new ConflictedDataException("Невозможна публикация отмененного события");
                }
                event.setState(PUBLISHED);
            }
            if (stateAction.equals(REJECT_EVENT)) {
                event.setState(CANCELED);
            }
        }
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(long eventId, long userId, UpdateEventUserRequest request) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        if (isEventPublished(event)) {
            throw new ValidationException("Невозможно изменить событие в статусе PUBLISHED");
        }
        updateEventFields(event, request);
        if (request.hasStateAction()) {
            StateAction stateAction = request.getStateAction();
            if (stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(PENDING);
            }
            if (stateAction.equals(CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
        }
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public Collection<EventShortDto> getUsersEvents(int from, int size, long userId) {
        checkUser(userId);
        return eventRepository.findByInitiatorId(PageRequest.of(from, size), userId)
                .stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUsersEvent(long userId, long eventId) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        return EventMapper.mapToEventFullDto(event);
    }

    private void updateEventFields(Event event, UpdateEventRequestDto request) {
        if (request.hasCategory()) {
            Category category = categoryRepository.findById(request.getCategory()).orElseThrow(
                    () -> new NotFoundException("Категория с id=" + request.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (request.hasEventDate()) {
            LocalDateTime eventDate = LocalDateTime.parse(request.getEventDate(), formatter);
            checkEventDate(eventDate);
            event.setEventDate(eventDate);
        }
        if (request.hasLocation()) {
            Location newLocation = checkLocation(request.getLocation());
            event.setLocation(newLocation);
        }
        EventMapper.updateBaseFields(request, event);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            //TODO Сделать код ошибки 409
            throw new ConflictedDataException("должно содержать дату, которая еще не наступила");
        }
    }

    private boolean isEventPublished(Event event) {
        return event.getState().equals(PUBLISHED);
    }

    public User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("user с " + userId + " не найден"));
    }

    public Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с " + eventId + " не найдено"));
    }

    private Location checkLocation(Location location) {
        Location newLoc = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if (newLoc == null) {
            locationRepository.save(location);
            newLoc = location;
        }
        return newLoc;
    }
}