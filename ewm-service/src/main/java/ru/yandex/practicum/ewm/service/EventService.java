package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.dto.event.NewEventDto;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(long userId, NewEventDto dto);

    EventFullDto getEvent(long eventId);

    Collection<EventFullDto> getPublishedEvents(int from,
                                                int size,
                                                String sortBy,
                                                String text,
                                                List<Long> catIds,
                                                Boolean paid,
                                                String startDateTime,
                                                String endDateTime,
                                                Boolean onlyAvailable);
}