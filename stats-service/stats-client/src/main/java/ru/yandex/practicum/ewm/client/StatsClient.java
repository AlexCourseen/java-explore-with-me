package ru.yandex.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.ewm.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.dto.ViewStatsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(EndpointHitDto request) {
        return post("/hit", request);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        } else {
            parameters.put("uris", "");
        }
        parameters.put("unique", unique);

        ViewStatsDto[] response = rest.getForObject(path, ViewStatsDto[].class, parameters);

        return response != null ? List.of(response) : List.of();
    }
}
