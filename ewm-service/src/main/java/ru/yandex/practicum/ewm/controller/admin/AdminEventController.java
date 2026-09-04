package ru.yandex.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.dto.event.UpdateEventRequestDto;
import ru.yandex.practicum.ewm.model.State;
import ru.yandex.practicum.ewm.service.CommentService;
import ru.yandex.practicum.ewm.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping()
    public Collection<EventFullDto> getEvents(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @PositiveOrZero int size,
                                              @RequestParam(required = false) List<State> states,
                                              @RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd) {
        return eventService.getEventsByAdmin(from, size, states, users, categories, rangeStart, rangeEnd);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @RequestBody @Valid UpdateEventRequestDto request) {
        return eventService.updateEventByAdmin(eventId, request);
    }

    @DeleteMapping("/{eventId}/comments/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delComment(@PathVariable long eventId,
                           @PathVariable long commId) {
        commentService.delCommentByAdmin(eventId, commId);
    }
}