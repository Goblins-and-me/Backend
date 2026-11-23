package com.shop.SweetStory.auth.controller;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.LoginRequest;
import com.shop.SweetStory.auth.dto.MessageResponse;
import com.shop.SweetStory.auth.dto.RegisterRequest;
import com.shop.SweetStory.auth.service.AuthService;
import com.shop.SweetStory.auth.service.AuthService.AuthSession;
import com.shop.SweetStory.common.security.JwtAuthFilter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * REST controller for user authentication and registration mapped to {@code /api/auth}.
 * Handles sign-up, sign-in, and sign-out requests while managing the
 * {@code HttpOnly} session JWT cookie on the client.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final long expirationMs;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    /**
     * Creates an authentication controller with token lifetime and cookie attributes.
     *
     * @param authService service for authentication and account management
     * @param expirationMs cookie lifetime in milliseconds (defaults to 24 hours)
     * @param cookieSecure whether the cookie is sent only over HTTPS
     * @param cookieSameSite {@code SameSite} policy protecting the cookie against cross-site requests
     */
    public AuthController(
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
     * Registers a new user and sets the session token cookie.
     *
     * @param request registration payload validated via Jakarta Bean Validation
     * @return {@code 201 Created} response with a {@code Set-Cookie} header and session details
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Register the user and obtain the session containing the token and profile DTO
        AuthSession session = authService.register(request);
        ResponseCookie cookie = buildAuthCookie(
                session.token(),
                Duration.ofMillis(expirationMs),
                cookieSecure,
                cookieSameSite
        );

        // Return 201 Created with the JWT in Set-Cookie and profile data in the response body
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(session.response());
    }

    /**
     * Authenticates a user by credentials and writes the session token cookie.
     *
     * @param request login credentials (email and password)
     * @return {@code 200 OK} response with a {@code Set-Cookie} header and profile details
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Verify credentials and create a new session
        AuthSession session = authService.login(request);
        ResponseCookie cookie = buildAuthCookie(
                session.token(),
                Duration.ofMillis(expirationMs),
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(session.response());
    }

    /**
     * Ends the user session by clearing the authentication cookie.
     *
     * @return {@code 200 OK} response with a clearing {@code Set-Cookie} header and logout message
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        // Overwrite the cookie with an empty value and zero maxAge to delete it immediately in the browser
        ResponseCookie clearedCookie = buildAuthCookie(
                "",
                Duration.ZERO,
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .body(new MessageResponse("Вы успешно вышли из системы"));
    }

    /**
     * Builds an {@code HttpOnly} authentication cookie scoped to the root path {@code /}.
     *
     * @param value token value or an empty string when clearing the cookie
     * @param maxAge cookie lifetime
     * @param secure {@code Secure} flag
     * @param sameSite {@code SameSite} attribute value
     * @return built {@link ResponseCookie} instance
     */
    private ResponseCookie buildAuthCookie(
            String value,
            Duration maxAge,
            boolean secure,
            String sameSite
    ) {
        // httpOnly(true) hides the token from JS to reduce the risk of theft via XSS
        return ResponseCookie.from(JwtAuthFilter.AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(maxAge)
                .build();
    }
}