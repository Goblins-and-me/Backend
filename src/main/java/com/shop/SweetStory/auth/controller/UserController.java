package com.shop.SweetStory.auth.controller;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.MessageResponse;
import com.shop.SweetStory.auth.dto.UpdateProfileRequest;
import com.shop.SweetStory.auth.service.AuthService;
import com.shop.SweetStory.auth.service.AuthService.AuthSession;
import com.shop.SweetStory.common.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * REST-контроллер операций с профилем текущего пользователя по пути {@code /api/users}.
 * Позволяет частично обновлять профиль и удалять учётную запись с обновлением или сбросом
 * {@code HttpOnly}-cookie сессии.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final long expirationMs;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    /**
     * Создаёт контроллер управления профилем пользователя с параметрами cookie сессии.
     *
     * @param authService сервис работы с профилями и учётными записями
     * @param expirationMs время жизни обновлённой cookie в миллисекундах (по умолчанию 24 часа)
     * @param cookieSecure признак передачи cookie только по защищённому каналу HTTPS
     * @param cookieSameSite политика {@code SameSite} для cookie аутентификации
     */
    public UserController(
            AuthService authService,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs,
            @Value("${app.jwt.cookie-secure:false}") boolean cookieSecure,
            @Value("${app.jwt.cookie-same-site:Lax}") String cookieSameSite
    ) {
        this.authService = authService;
        this.expirationMs = expirationMs;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    /**
     * Обновляет профиль текущего аутентифицированного пользователя и перевыпускает cookie сессии.
     *
     * @param authentication объект текущей аутентификации пользователя
     * @param request данные для частичного обновления профиля
     * @return ответ {@code 200 OK} с заголовком {@code Set-Cookie} и обновлёнными данными профиля
     */
    @PatchMapping("/me")
    public ResponseEntity<AuthResponse> updateMe(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {
        // Извлекаем email текущего пользователя и обновляем данные профиля с выпуском нового токена
        String email = requireAuthenticatedEmail(authentication);
        AuthSession session = authService.updateProfile(email, request);
        ResponseCookie refreshedCookie = buildAuthCookie(
                session.token(),
                Duration.ofMillis(expirationMs),
                cookieSecure,
                cookieSameSite
        );

        // Возвращаем 200 OK с обновлённой cookie в заголовке и профилем в теле ответа
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshedCookie.toString())
                .body(session.response());
    }

    /**
     * Удаляет учётную запись текущего пользователя и сбрасывает cookie аутентификации.
     *
     * @param authentication объект текущей аутентификации пользователя
     * @return ответ {@code 200 OK} с очищающим заголовком {@code Set-Cookie} и сообщением об удалении
     */
    @DeleteMapping("/me")
    public ResponseEntity<MessageResponse> deleteMe(Authentication authentication) {
        // Проверяем аутентификацию и удаляем аккаунт текущего пользователя
        String email = requireAuthenticatedEmail(authentication);
        authService.deleteCurrentAccount(email);

        // Сбрасываем cookie пустым значением и нулевым maxAge для удаления в браузере
        ResponseCookie clearedCookie = buildAuthCookie(
                "",
                Duration.ZERO,
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .body(new MessageResponse("Аккаунт успешно удален"));
    }

    /**
     * Возвращает email аутентифицированного пользователя либо выбрасывает исключение,
     * если контекст аутентификации отсутствует или не содержит имени пользователя.
     */
    private String requireAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("unauthenticated");
        }
        return authentication.getName();
    }

    /**
     * Формирует {@code HttpOnly}-cookie аутентификации для корневого пути {@code /}.
     */
    private ResponseCookie buildAuthCookie(
            String value,
            Duration maxAge,
            boolean secure,
            String sameSite
    ) {
        // httpOnly(true) запрещает доступ к cookie из JavaScript для защиты от XSS
        return ResponseCookie.from(JwtAuthFilter.AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(maxAge)
                .build();
    }
}