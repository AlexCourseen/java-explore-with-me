package ru.yandex.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit mapToHit(EndpointHitDto request) {
        Hit hit = new Hit();
        hit.setIp(request.getIp());
        hit.setUri(request.getUri());
        hit.setCreated(LocalDateTime.parse(request.getTimestamp(),formatter));
        hit.setApp(request.getApp());
        return hit;
    }
}
