package com.example.sportsshop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ── Trang HTML & static resources ──
                        .requestMatchers("/", "/cart", "/seller", "/login", "/shop",
                                "/history", "/index.html", "/static/**", "/*.*").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // ── Auth: đăng nhập + đăng ký ──
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── Products: ai cũng xem được ──
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // ── Products: chỉ ADMIN mới CUD ──
                        .requestMatchers(HttpMethod.POST,   "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // ── Orders: chỉ CUSTOMER ──
                        .requestMatchers(HttpMethod.POST, "/api/orders/checkout").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/orders/checkout-cart").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET,  "/api/orders/my-history").hasRole("CUSTOMER")

                        // ── Cart: chỉ CUSTOMER ──
                        .requestMatchers("/api/cart/**").hasRole("CUSTOMER")

                        // ── Phần còn lại: phải đăng nhập ──
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}