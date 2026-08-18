package ru.yandex.practicum.ewm.controller.publics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.client.StatsClient;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.event.EventFullDto;
import ru.yandex.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final StatsClient statsClient;
    private final static String APP = "ewm-service";

    @GetMapping()
    public Collection<EventFullDto> getEvents(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @PositiveOrZero int size,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(required = false) String text,
                                              @RequestParam(required = false) List<Long> ids,
                                              @RequestParam(required = false) List<Long> catIds,
                                              @RequestParam(required = false) Boolean paid,
                                              @RequestParam(required = false) String startDateTime,
                                              @RequestParam(required = false) String endDateTime,
                                              @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                              HttpServletRequest request) {
        EndpointHitDto hitStats = new EndpointHitDto();
        hitStats.setApp(APP);
        hitStats.setIp(request.getRemoteAddr());
        hitStats.setUri(request.getRequestURI());
        hitStats.setTimestamp(LocalDateTime.now().toString());
        statsClient.createHit(hitStats);
        return eventService.getPublishedEvents(from, size, sort, text, ids, catIds, paid, startDateTime, endDateTime,
                onlyAvailable);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable long eventId, HttpServletRequest request) {
        EndpointHitDto hitStats = new EndpointHitDto();
        hitStats.setApp(APP);
        hitStats.setIp(request.getRemoteAddr());
        hitStats.setUri(request.getRequestURI());
        hitStats.setTimestamp(LocalDateTime.now().toString());
        statsClient.createHit(hitStats);
        return eventService.getEvent(eventId);
    }
}