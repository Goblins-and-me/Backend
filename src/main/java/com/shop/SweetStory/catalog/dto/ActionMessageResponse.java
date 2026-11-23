// ActionMessageResponse.java
package com.shop.SweetStory.catalog.dto;

/**
 * Response DTO carrying the result of an action performed on a catalog product.
 *
 * @param status operation execution status
 * @param message message describing the operation result
 */
public record ActionMessageResponse(
        String status,
        String message
) {
}