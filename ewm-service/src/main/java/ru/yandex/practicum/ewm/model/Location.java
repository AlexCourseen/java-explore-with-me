package ru.yandex.practicum.ewm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "locations", schema = "public")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lat")
    @NotNull(message = "Широта не может быть пустой")
    private float lat;

    @Column(name = "lon")
    @NotNull(message = "Долгота не может быть пустой")
    private float lon;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Float.compare(lat, location.lat) == 0 && Float.compare(lon, location.lon) == 0 && Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lat, lon);
    }
}