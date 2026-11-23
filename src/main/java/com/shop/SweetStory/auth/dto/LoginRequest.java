package com.shop.SweetStory.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for signing in a user with email format and required-field validation.
 *
 * @param email user's email address (required field, email format)
 * @param password user's password (required field)
 */
public record LoginRequest(
        @Email
        @NotBlank(message = "login must mot be empty")
        String email,

        @NotBlank(message = "password must not be empty")
        String password
) {
}