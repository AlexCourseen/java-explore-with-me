package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.user.NewUserRequestDto;
import ru.yandex.practicum.ewm.dto.user.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers(int from, int size);

    UserDto createUser(NewUserRequestDto request);

    void delUser(long userId);
}
