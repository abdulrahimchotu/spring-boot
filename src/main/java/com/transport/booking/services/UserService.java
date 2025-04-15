package com.transport.booking.services;

import com.transport.booking.dto.UserDto;
import com.transport.booking.dto.UserLoginDto;
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

    public User register(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRole(User.UserRole.valueOf(userDto.getRole().toUpperCase()));
        
        return userRepository.save(user);
    }

    public LoginResponseDto login(UserLoginDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!userDto.getPassword().equals(user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("ROLE", user.getRole().toString());
        claims.put("Userid", user.getId());
    

        String token = jwtUtil.generateToken(claims, user.getUsername());

        return new LoginResponseDto(token, user.getUsername(), user.getRole().toString());
    }
}
    
