package com.shop.SweetStory.common.security;

import com.shop.SweetStory.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Service for generating, signature-verifying, and extracting claims from JSON Web Tokens (JWT).
 * Uses a symmetric HMAC-SHA key and the JJWT library to create tokens containing
 * {@link User} profile data and parse token claims during request authentication.
 */
@Service
public class JwtService {

    // Symmetric HMAC-SHA signing key built from the configured secret string
    private final SecretKey signingKey;
    // Lifetime of issued JWT tokens in milliseconds
    private final long expirationMs;

    /**
     * Creates a JWT service using the signing secret and token expiration time from configuration.
     *
     * @param secret secret string used to derive the HMAC signing key
     * @param expirationMs token validity duration in milliseconds
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        // Build the HMAC-SHA cryptographic key from UTF-8 bytes of the secret
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // Store the token expiration duration
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT token containing profile data and permissions for the given user.
     * Includes standard issued-at and expiration timestamps, the user's email as subject,
     * and custom claims ({@code userId}, {@code name}, {@code email}, {@code phone},
     * {@code avatar}, {@code is_admin}).
     *
     * @param user user entity for which the token is generated
     * @return compact string representation of the signed JWT token
     */
    public String generateToken(User user) {
        // Capture current timestamp and compute token expiration time
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        // Safely normalize user attributes, replacing null values with empty strings or false
        boolean isAdmin = Boolean.TRUE.equals(user.getIsAdmin());
        String avatar = user.getAvatar() != null ? user.getAvatar() : "";
        String phone = user.getPhone() != null ? user.getPhone() : "";
        String name = user.getName() != null ? user.getName() : "";

        // Assemble, sign with the HMAC key, and compact the JWT token
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", name)
                .claim("email", user.getEmail())
                .claim("phone", phone)
                .claim("avatar", avatar)
                .claim("is_admin", isAdmin)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Verifies the signature and validity of a JWT token, returning its claims payload.
     *
     * @param token string representation of the JWT token
     * @return {@link Claims} object containing the token payload
     * @throws JwtException if the token is malformed, expired, has an invalid signature, or is otherwise invalid
     */
    public Claims parseAndValidate(String token) throws JwtException {
        // Verify the token signature with signingKey and extract the payload
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts and converts the {@code userId} claim to {@link Long}.
     * Handles possible numeric JSON deserialization types ({@link Integer}, {@link Long}, or string).
     *
     * @param token string representation of the JWT token
     * @return user identifier
     * @throws JwtException if the {@code userId} claim is missing or the token is invalid
     * @throws NumberFormatException if a string {@code userId} value cannot be parsed as {@code Long}
     */
    public Long extractUserId(String token) {
        // Read the userId claim from the verified token payload
        Object rawId = parseAndValidate(token).get("userId");
        if (rawId == null) {
            throw new JwtException("Missing userId claim in JWT");
        }
        // Convert numeric value to Long according to the parsed runtime type
        if (rawId instanceof Integer i) {
            return i.longValue();
        }
        if (rawId instanceof Long l) {
            return l;
        }
        return Long.parseLong(String.valueOf(rawId));
    }

    /**
     * Extracts the user's email from the {@code subject} field of a verified JWT token.
     *
     * @param token string representation of the JWT token
     * @return user email ({@code subject})
     * @throws JwtException if the token is invalid or expired
     */
    public String extractEmail(String token) {
        return parseAndValidate(token).getSubject();
    }

    /**
     * Extracts the administrator privilege flag from the {@code is_admin} claim of a JWT token.
     *
     * @param token string representation of the JWT token
     * @return {@code true} if {@code is_admin == true} is explicitly set in the token, {@code false} otherwise
     * @throws JwtException if the token is invalid or expired
     */
    public boolean extractIsAdmin(String token) {
        Boolean flag = parseAndValidate(token).get("is_admin", Boolean.class);
        return Boolean.TRUE.equals(flag);
    }
}