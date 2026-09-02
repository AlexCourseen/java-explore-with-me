package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.comment.CommentDto;
import ru.yandex.practicum.ewm.dto.comment.NewCommentDto;
import ru.yandex.practicum.ewm.exception.ConflictedDataException;
import ru.yandex.practicum.ewm.exception.NoAccessException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.exception.ValidationException;
import ru.yandex.practicum.ewm.mapper.CommentMapper;
import ru.yandex.practicum.ewm.model.Comment;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.State;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventServiceImpl eventService;
    private final CommentRepository commentRepository;

    @Override
    public Collection<CommentDto> getComments(long eventId) {
        eventService.checkEvent(eventId);
        return commentRepository.getEventComments(eventId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public CommentDto getComment(long eventId, long commId) {
        eventService.checkEvent(eventId);
        return CommentMapper.mapToCommentDto(checkComment(commId));
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long eventId, NewCommentDto request) {
        User author = eventService.checkUser(userId);
        Event event = eventService.checkEvent(eventId);
        checkCommentText(request);
        checkEventState(event);
        commentRepository.getEventComments(eventId).stream()
                .filter(e -> e.getAuthor().equals(author))
                .findFirst()
                .ifPresent(e ->
                {
                    throw new ConflictedDataException("Нельзя повторно создать комментарий");
                });
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setText(request.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(comment);

    }

    @Override
    @Transactional
    public CommentDto updateComment(long userId, long eventId, long commId, NewCommentDto request) {
        User author = eventService.checkUser(userId);
        eventService.checkEvent(eventId);
        checkCommentText(request);
        Comment comment = checkComment(commId);
        if (!comment.getAuthor().equals(author)) {
            throw new NoAccessException("Комментарий может редактировать только его автор");
        }
        comment.setText(request.getText());
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public void delCommentByAuthor(long userId, long eventId, long commId) {
        eventService.checkUser(userId);
        eventService.checkEvent(eventId);
        commentRepository.delete(checkComment(commId));
    }

    @Override
    public void delCommentByAdmin(long eventId, long commId) {
        eventService.checkEvent(eventId);
        commentRepository.delete(checkComment(commId));
    }

    private void checkCommentText(NewCommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Нельзя добавить пустой комментарий");
        }
    }

    private void checkEventState(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Нельзя добавить комментарий к неопубликованному событию");
        }
    }

    private Comment checkComment(long commId) {
        return commentRepository.findById(commId).orElseThrow(() ->
                new NotFoundException("Комментарий не найден"));
    }
}
