package com.shop.SweetStory.common.config;

import com.shop.SweetStory.common.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * Spring Security and CORS configuration for the SweetStory application.
 * Configures a stateless filter chain, endpoint access rules, JWT authentication
 * via {@link JwtAuthFilter}, allowed CORS origins, and a BCrypt password encoder.
 */
@Configuration
public class SecurityConfig {

    // Custom filter for validating JWTs and populating SecurityContext
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Creates the security configuration with the injected JWT filter.
     *
     * @param filter JWT authentication filter
     */
    public SecurityConfig(JwtAuthFilter filter) {
        // Store reference to the token authentication filter
        this.jwtAuthFilter = filter;
    }

    /**
     * Builds the primary HTTP security filter chain.
     * Disables CSRF (for stateless token-based architecture), sets session management
     * to {@code STATELESS}, defines route authorization rules, and registers {@link JwtAuthFilter}
     * ahead of the standard username/password authentication filter.
     *
     * @param http Spring Security HTTP security builder
     * @return built {@link SecurityFilterChain}
     * @throws Exception if building the security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection since a stateless token model without server sessions is used
                .csrf(AbstractHttpConfigurer::disable)
                // Configure stateless session policy — authentication state is not stored on the server between requests
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Define authorization rules for URL path groups and HTTP methods
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to registration, login, and logout endpoints
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/logout").permitAll()
                        // Allow only GET requests to the public product catalog for all users
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                        // Restrict administrative endpoints to users with the ADMIN role
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Require authentication for all remaining requests
                        .anyRequest().authenticated()
                )
                // Register the JWT filter before UsernamePasswordAuthenticationFilter to process incoming tokens
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Build and return the resulting security filter chain
        return http.build();
    }

    /**
     * Creates a CORS configuration source for cross-origin requests from frontend applications.
     * Allows configured origins, standard HTTP methods, any headers, and credentials
     * (such as cookies) across all application paths ({@code /**}).
     *
     * @param allowedOrigins list of allowed origin URLs from configuration (with local and production fallbacks)
     * @return CORS configuration source {@link CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,https://sweetstory-sigma.vercel.app}") List<String> allowedOrigins
    ) {
        // Create CORS configuration object and set access parameters
        CorsConfiguration config = new CorsConfiguration();
        // Specify allowed frontend origins
        config.setAllowedOrigins(allowedOrigins);
        // Allow standard HTTP methods, including OPTIONS preflight requests
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Allow any request headers
        config.setAllowedHeaders(List.of("*"));
        // Allow sending credentials (such as HttpOnly cookies) in cross-origin requests
        config.setAllowCredentials(true);

        // Register the CORS configuration for all URL paths in the application
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Registers a password encoder bean backed by the BCrypt algorithm.
     *
     * @return {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt with default settings for secure password hashing
        return new BCryptPasswordEncoder();
    }
}