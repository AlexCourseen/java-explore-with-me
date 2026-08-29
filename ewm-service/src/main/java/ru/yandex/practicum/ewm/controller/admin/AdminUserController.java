package ru.yandex.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.dto.user.NewUserRequestDto;
import ru.yandex.practicum.ewm.dto.user.UserDto;
import ru.yandex.practicum.ewm.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping()
    public Collection<UserDto> getUsers(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return userService.getUsers(from, size);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequestDto request) {
        return userService.createUser(request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delUser(@PathVariable long userId) {
        userService.delUser(userId);
    }
}
