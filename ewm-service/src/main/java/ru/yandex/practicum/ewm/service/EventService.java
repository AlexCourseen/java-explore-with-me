package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.dto.event.EventShortDto;
import ru.yandex.practicum.ewm.dto.event.NewEventDto;
import ru.yandex.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.ewm.model.State;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(long userId, NewEventDto dto);

    EventFullDto getEvent(long eventId);

    Collection<EventShortDto> getPublishedEvents(int from,
                                                 int size,
                                                 String sortBy,
                                                 String text,
                                                 List<Long> catIds,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable);

    Collection<EventFullDto> getEventsByAdmin(int from,
                                              int size,
                                              List<State> states,
                                              List<Long> users,
                                              List<Long> categories,
                                              String rangeStart,
                                              String rangeEnd);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request);

    EventFullDto updateEventByUser(long eventId, long userId, UpdateEventUserRequest request);
}
