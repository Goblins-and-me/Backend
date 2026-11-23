package com.shop.SweetStory.auth.dto;

/**
 * DTO текстового ответа со статусом выполнения операции и сообщением для клиента.
 *
 * @param status статус выполнения операции
 * @param message текстовое сообщение для клиента
 */
public record MessageResponse(
        String status,
        String message
) {
    /**
     * Создаёт успешный ответ со статусом {@code "success"} и указанным сообщением.
     *
     * @param message текстовое сообщение для клиента
     */
    public MessageResponse(String message) {
        this("success", message);
    }
}