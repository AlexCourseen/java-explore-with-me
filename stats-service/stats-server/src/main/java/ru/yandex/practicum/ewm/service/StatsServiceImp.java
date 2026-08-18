package ru.yandex.practicum.ewm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.mapper.HitMapper;
import ru.yandex.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImp implements StatsService {
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void createHit(EndpointHitDto request) {
        statsRepository.save(HitMapper.mapToHit(request));
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        List<ViewStatsDto> stats;
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
        }

        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                stats = statsRepository.findAllStatsByUriWithUniqueIp(startTime, endTime, uris);
            } else {
                stats = statsRepository.findStatsWithUniqueIp(startTime, endTime);
            }
        } else {
            if (uris != null && !uris.isEmpty()) {
                stats = statsRepository.findAllStatsByUri(startTime, endTime, uris);
            } else {
                stats = statsRepository.findAllStats(startTime, endTime);

            }
        }
        return stats;
    }
}
