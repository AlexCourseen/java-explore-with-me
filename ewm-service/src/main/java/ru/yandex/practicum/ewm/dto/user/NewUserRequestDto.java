package ru.yandex.practicum.ewm.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserRequestDto {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя должно быть от 2 до 250 символов")
    private String name;

    @NotBlank(message = "email не может быть пустым")
    @Email
    @Size(min = 6, max = 254, message = "email должно быть от 6 до 254 символов")
    private String email;

}
