package com.transport.booking.services;

import com.transport.booking.dto.UserDto;
import com.transport.booking.dto.LoginResponseDto;
import com.transport.booking.entities.User;
import com.transport.booking.exceptions.AuthenticationException;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.repositories.UserRepository;
import com.transport.booking.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void register(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // Store password as-is
        user.setEmail(userDto.getEmail());
        user.setIsAdmin(false);
        
        userRepository.save(user);
    }

    public LoginResponseDto login(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        // Direct string comparison of passwords
        if (!userDto.getPassword().equals(user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", user.getIsAdmin());

        String token = jwtUtil.generateToken(claims, user.getUsername());

        return new LoginResponseDto(token, user.getUsername(), user.getIsAdmin());
    }
}
    
