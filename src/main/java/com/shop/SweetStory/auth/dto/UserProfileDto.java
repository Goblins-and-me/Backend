package com.shop.SweetStory.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO публичного профиля пользователя, передаваемый клиенту в ответах API.
 *
 * @param id идентификатор пользователя
 * @param name имя пользователя
 * @param email электронная почта пользователя
 * @param phone контактный телефон пользователя
 * @param avatar аватар пользователя
 * @param isAdmin признак прав администратора (в JSON сериализуется как {@code is_admin})
 */
public record UserProfileDto(
        Long id,
        String name,
        String email,
        String phone,
        String avatar,
        @JsonProperty("is_admin")
        Boolean isAdmin
) {
}