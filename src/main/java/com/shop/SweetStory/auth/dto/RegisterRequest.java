package com.shop.SweetStory.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO запроса на регистрацию нового пользователя с правилами валидации полей.
 *
 * @param email электронная почта пользователя (обязательное поле, формат email)
 * @param name имя пользователя (обязательное поле, до 150 символов)
 * @param password пароль пользователя (обязательное поле, от 6 до 100 символов)
 * @param phone контактный телефон пользователя (обязательное поле)
 */
public record RegisterRequest(

        @Email
        @NotBlank(message = "email must not be empty")
        String email,

        @NotBlank(message = "name must not be empty")
        @Size(max = 150, message = "length must be under 150 symbols")
        String name,

        @NotBlank(message = "password must not be empty")
        @Size(max = 100, min = 6, message = "length must be longer then 6 symbols")
        String password,

        @NotBlank(message = "phone must not be empty")
        String phone
) {
}