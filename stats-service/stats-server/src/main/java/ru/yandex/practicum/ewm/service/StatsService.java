package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;

import java.util.Collection;
import java.util.List;

public interface StatsService {
    void createHit(EndpointHitDto hit);

    Collection<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);


}
