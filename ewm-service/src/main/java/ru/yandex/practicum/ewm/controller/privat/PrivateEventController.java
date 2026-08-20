package ru.yandex.practicum.ewm.controller.privat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.dto.event.EventShortDto;
import ru.yandex.practicum.ewm.dto.event.NewEventDto;
import ru.yandex.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.ewm.service.EventService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@PathVariable long userId,
                                    @RequestBody @Valid NewEventDto request) {
        return eventService.createEvent(userId, request);
    }

    @GetMapping("/{userId}/events")
    public Collection<EventShortDto> getUsersEvents(@PathVariable long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return eventService.getUsersEvents(from, size, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUsersEvent(@PathVariable long userId,
                                      @PathVariable long eventId) {
        return eventService.getUsersEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto getUsersEvent(@PathVariable long userId,
                                      @PathVariable long eventId,
                                      @RequestBody UpdateEventUserRequest request) {
        return eventService.updateEventByUser(eventId, userId, request);
    }
}
