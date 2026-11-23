package com.shop.SweetStory.auth.dto;

/**
 * Request DTO for partially updating the current user's profile details.
 *
 * @param name new user name
 * @param phone new contact phone number
 * @param avatar new user avatar identifier or URL
 */
public record UpdateProfileRequest(
        String name,
        String phone,
        String avatar
) {
}