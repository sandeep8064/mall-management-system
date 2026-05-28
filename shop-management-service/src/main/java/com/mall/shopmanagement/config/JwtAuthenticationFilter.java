package com.mall.shopmanagement.config;

import com.mall.shopmanagement.dto.UserPrincipal;
import com.mall.shopmanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractSubject(token);
                Long userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserPrincipal principal = UserPrincipal.builder()
                            .id(userId)
                            .email(email)
                            .role(role)
                            .build();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Let the request proceed unauthenticated; entry points will catch unauthorized access
            logger.debug("JWT verification failed in Shop Service: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
