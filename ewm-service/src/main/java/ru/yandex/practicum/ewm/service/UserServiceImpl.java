package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.user.NewUserRequestDto;
import ru.yandex.practicum.ewm.dto.user.UserDto;
import ru.yandex.practicum.ewm.exception.DuplicatedDataException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.mapper.UserMapper;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getUsers(int from, int size) {
        return userRepository.getUsers(PageRequest.of(from, size)).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new DuplicatedDataException("user c email: " + request.getEmail() + " уже существует");
        }
        User user = UserMapper.mapNewUserRequestDtoToUser(request);
        userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void delUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("user с " + userId + " не найден"));
        userRepository.delete(user);
    }
}
