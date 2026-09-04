package ru.yandex.practicum.ewm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 10, max = 50, message = "Текст комментария должен быть от 1 до 2000 символов")
    private String text;

    @JoinColumn(name = "author_id")
    @ManyToOne
    private User author;

    @JoinColumn(name = "event_id")
    @ManyToOne
    private Event event;

    @Column(name = "created")
    private LocalDateTime created;

}
