package ru.yandex.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.dto.user.NewUserRequestDto;
import ru.yandex.practicum.ewm.dto.user.UserCommentDto;
import ru.yandex.practicum.ewm.dto.user.UserDto;
import ru.yandex.practicum.ewm.dto.user.UserShortDto;
import ru.yandex.practicum.ewm.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapNewUserRequestDtoToUser(NewUserRequestDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        return user;
    }

    public static UserShortDto mapToUserShortDto(User user) {
        UserShortDto dto = new UserShortDto();
        dto.setEmail(user.getEmail());
        dto.setId(user.getId());
        return dto;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setId(user.getId());
        return dto;
    }

    public static UserCommentDto mapToUserCommentDto(User user) {
        UserCommentDto dto = new UserCommentDto();
        dto.setName(user.getName());
        dto.setId(user.getId());
        return dto;
    }
}
