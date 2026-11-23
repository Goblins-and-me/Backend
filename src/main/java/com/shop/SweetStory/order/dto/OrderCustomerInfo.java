// OrderCustomerInfo.java
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO containing customer contact details for an order with field validation rules.
 *
 * @param name customer name (required non-blank field)
 * @param phone customer phone number (required non-blank field)
 * @param email customer email address (required field in email format)
 */
public record OrderCustomerInfo(
        @NotBlank
        String name,

        @NotBlank
        String phone,

        @NotBlank
        @Email
        String email

) {
}