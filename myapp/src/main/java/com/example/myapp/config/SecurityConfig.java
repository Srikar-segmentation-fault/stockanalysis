package com.example.myapp.config;

import com.example.myapp.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public health check endpoints
                        .requestMatchers("/api/health", "/actuator/health", "/actuator/info").permitAll()
                        // Public auth endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login").permitAll()
                        // Admin-only: delete user
                        .requestMatchers(HttpMethod.DELETE, "/auth/users/**").hasRole("ADMIN")
                        // Authenticated users: read user data
                        .requestMatchers("/auth/users/**").authenticated()
                        // Stock read: any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/stocks/**").authenticated()
                        // Stock write: ADMIN only
                        .requestMatchers(HttpMethod.POST, "/api/stocks/**").hasRole("ADMIN")
                        // Portfolio: authenticated user (ownership enforced in service)
                        .requestMatchers("/api/portfolio/**").authenticated()
                        // All other requests require authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
