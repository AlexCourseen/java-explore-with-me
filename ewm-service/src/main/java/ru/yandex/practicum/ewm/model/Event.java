package ru.yandex.practicum.ewm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "views")
    private long views;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "annotation")
    @NotBlank(message = "Краткое описание события не может быть пустым")
    @Size(min = 20, max = 2000, message = "Краткое описание события должно быть от 20 до 2000 символов")
    private String annotation;

    @JoinColumn(name = "category_id")
    @ManyToOne
    @NotNull(message = "Категория события не может быть пустым")
    private Category category;

    @JoinColumn(name = "initiator_id")
    @ManyToOne
    private User initiator;

    @JoinColumn(name = "location_id")
    @ManyToOne
    @NotNull(message = "Место проведения события не может быть пустым")
    private Location location;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "confirmed_requests")
    private int confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "description")
    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов")
    private String description;

    @Column(name = "title")
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
}
