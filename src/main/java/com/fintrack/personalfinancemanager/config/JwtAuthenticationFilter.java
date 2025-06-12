package com.fintrack.personalfinancemanager.config;

import com.fintrack.personalfinancemanager.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter for JWT authentication.
 * Extracts and validates the JWT token from the Authorization header.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Filters incoming requests and authenticates users based on JWT tokens.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet exception occurs
     * @throws IOException if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");

        String userId = null;
        String jwt = null;

        // Extract JWT token from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                userId = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Error extracting username from token", e);
            }
        }

        // Authenticate user if token is valid and user is not already authenticated
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Long userIdLong = Long.parseLong(userId);
                if (userService.existsById(userIdLong)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userIdLong, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (NumberFormatException e) {
                logger.error("Error parsing user ID from token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}