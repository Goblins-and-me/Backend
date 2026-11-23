package com.shop.SweetStory.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security filter executed once per HTTP request.
 * Extracts the JWT token from the {@value #AUTH_COOKIE_NAME} cookie, validates and parses it
 * via {@link JwtService}, builds an authentication token with user authorities, and places it
 * into {@link SecurityContextHolder} for downstream request authorization.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Name of the HTTP cookie storing the JWT access token.
     */
    public static final String AUTH_COOKIE_NAME = "access_token";

    // Service for validating tokens and extracting claims from JWTs
    private final JwtService jwtService;

    /**
     * Creates a JWT authentication filter with the injected token service.
     *
     * @param service service for parsing and validating JWT tokens
     */
    public JwtAuthFilter(JwtService service) {
        // Store reference to the JWT service
        this.jwtService = service;
    }

    /**
     * Executes filtering logic for an incoming HTTP request: reads the token from the cookie,
     * sets authentication in {@link SecurityContextHolder} if present and valid, and delegates
     * to the remainder of the filter chain.
     *
     * @param request incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain servlet and Spring Security filter chain
     * @throws ServletException if a servlet or filter error occurs
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Attempt to extract the JWT token from request cookies
        String token = extractTokenFromCookie(request);

        // Process the token only if non-blank and no authentication is already present
        if (token != null && !token.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Extract user identity details and admin flag from the token
                String email = jwtService.extractEmail(token);
                Long userId = jwtService.extractUserId(token);
                boolean isAdmin = jwtService.extractIsAdmin(token);

                // Build authorities list: all authenticated users receive the base USER role
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                // Also grant the ADMIN role if the flag is set in the token
                if (isAdmin) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                // Create authentication token with email as principal and userId in details
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                authentication.setDetails(userId);

                // Store authentication in the current thread's security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // Invalid or expired tokens simply leave authentication unset in the context
            }
        }

        // Continue processing the filter chain regardless of token presence or validity
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the cookie value for {@value #AUTH_COOKIE_NAME} from the HTTP request.
     *
     * @param request incoming HTTP request
     * @return token value from the cookie, or {@code null} if cookies are absent or not found
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        // Retrieve cookies from the request; return null if none are present
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        // Iterate through cookies to locate the access token
        for (Cookie cookie : cookies) {
            if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        // Return null if no cookie with the target name was found
        return null;
    }
}