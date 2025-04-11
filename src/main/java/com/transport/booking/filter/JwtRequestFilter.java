package com.transport.booking.filter;

import com.transport.booking.util.JwtUtil;
import com.transport.booking.exceptions.AuthenticationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> PUBLIC_PATHS = Arrays.asList("/auth/login", "/auth/register", "/swagger-ui/**", "/v3/api-docs/**");

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        try {
            // Skip authentication for public paths
            if (isPublicPath(request.getServletPath())) {
                chain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            
            // Check if Authorization header is missing or invalid
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new AuthenticationException("Invalid or expired token");
            }

            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            Boolean isAdmin = claims.get("isAdmin", Boolean.class);

            // Create authorities list based on user roles
            List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                isAdmin ? new SimpleGrantedAuthority("ROLE_ADMIN") : null
            ).stream()
                .filter(auth -> auth != null)
                .collect(Collectors.toList());

            // Create authentication token with proper authorities
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            );

            // Add request details to authentication token
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            chain.doFilter(request, response);

        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Optionally add paths that should never be filtered
        return request.getServletPath().equals("/health") || 
               request.getServletPath().equals("/metrics");
    }
}
