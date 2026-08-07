package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;
import ru.yandex.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.yandex.practicum.ewm.dto.ViewStatsDto(h.app, h.uri, count(h.ip))" +
            " from Hit h " +
            "where h.created between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) DESC")
    List<ViewStatsDto> findAllStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.yandex.practicum.ewm.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip))" +
            " from Hit h " +
            "where h.created between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) DESC")
    List<ViewStatsDto> findStatsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.yandex.practicum.ewm.dto.ViewStatsDto(h.app, h.uri, count(h.ip))" +
            " from Hit h " +
            "where h.created between ?1 and ?2 " +
            "and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) DESC")
    List<ViewStatsDto> findAllStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.yandex.practicum.ewm.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip))" +
            " from Hit h " +
            "where h.created between ?1 and ?2 " +
            "and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) DESC")
    List<ViewStatsDto> findAllStatsByUriWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

}