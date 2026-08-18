package ru.yandex.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.model.Hit;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {
    public static Hit mapToHit(EndpointHitDto request) {
        Hit hit = new Hit();
        hit.setIp(request.getIp());
        hit.setUri(request.getUri());
        hit.setCreated(LocalDateTime.parse(request.getTimestamp()));
        hit.setApp(request.getApp());
        return hit;
    }
}
