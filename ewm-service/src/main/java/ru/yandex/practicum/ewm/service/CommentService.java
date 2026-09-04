package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.comment.CommentDto;
import ru.yandex.practicum.ewm.dto.comment.NewCommentDto;

import java.util.Collection;

public interface CommentService {
    Collection<CommentDto> getComments(long eventId);

    CommentDto getComment(long eventId, long commId);

    CommentDto addComment(long userId, long eventId, NewCommentDto comment);

    CommentDto updateComment(long userId, long eventId, long commId, NewCommentDto request);

    void delCommentByAuthor(long userId, long eventId, long commId);

    void delCommentByAdmin(long eventId, long commId);
}
