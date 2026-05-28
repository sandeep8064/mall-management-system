package com.mall.productcatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        // Public Catalog Browsing (GET)
                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/{productId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/shop/{shopId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/subcategories/{categoryId}").permitAll()
                        // Category Writing (Admin only)
                        .requestMatchers(HttpMethod.POST, "/api/products/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/categories/**").hasRole("ADMIN")
                        // SubCategory & Product Writing (Shop Owner or Admin)
                        .requestMatchers("/api/products/subcategories/**").hasAnyRole("SHOP_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("SHOP_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("SHOP_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("SHOP_OWNER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
