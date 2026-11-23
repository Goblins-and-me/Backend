package com.shop.SweetStory.auth.dto;

/**
 * DTO ответа операций аутентификации и обновления профиля пользователя.
 * Содержит статус выполнения операции и данные профиля пользователя.
 *
 * @param status статус выполнения операции
 * @param user данные профиля пользователя
 */
public record AuthResponse(
        String status,
        UserProfileDto user
) {
    /**
     * Создаёт успешный ответ со статусом {@code "success"} и профилем пользователя.
     *
     * @param user данные профиля пользователя
     */
    public AuthResponse(UserProfileDto user) {
        this("success", user);
    }
}