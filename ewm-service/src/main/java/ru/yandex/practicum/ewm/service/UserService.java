package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.user.NewUserRequestDto;
import ru.yandex.practicum.ewm.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<UserDto> getUsers(int from, int size, List<Long> ids);

    UserDto createUser(NewUserRequestDto request);

    void delUser(long userId);
}
