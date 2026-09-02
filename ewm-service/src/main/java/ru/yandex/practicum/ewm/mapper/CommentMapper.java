package ru.yandex.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.dto.comment.CommentDto;
import ru.yandex.practicum.ewm.dto.comment.NewCommentDto;
import ru.yandex.practicum.ewm.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static Comment mapNewCommentDtoToComment(NewCommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthor(UserMapper.mapToUserCommentDto(comment.getAuthor()));
        dto.setEventId(comment.getEvent().getId());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}
