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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> PUBLIC_PATHS = Arrays.asList("/auth/login", "/auth/register","/ap/");

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        try {
            if (isPublicPath(request.getServletPath())) {
                chain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new AuthenticationException("Invalid or expired token");
            }

            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            String role = claims.get("ROLE", String.class);
            Integer userId = claims.get("Userid", Integer.class);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if ("ADMIN".equalsIgnoreCase(role)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            if ("USER".equalsIgnoreCase(role)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            if ("DRIVER".equalsIgnoreCase(role)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
            }


            UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            );
            authenticationToken.setDetails(userId);
            
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            
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


}
