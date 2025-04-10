package com.transport.booking.services;

import org.springframework.stereotype.Service;
import com.transport.booking.dto.UserDto;
import com.transport.booking.entities.User;
import com.transport.booking.repositories.UserRepository;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;
import com.transport.booking.exceptions.AuthenticationException;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String addUser(UserDto userDto) {
        // Validate input
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }

        // Check if username already exists
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // Note: In production, password should be encrypted
        user.setEmail(userDto.getEmail());
        user.setIsAdmin(false);
        
        userRepository.save(user);
        return "User added successfully";
    }

    public void login(UserDto userDto) {
        // Validate input
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }

        // Find user and validate credentials
        User user = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!user.getPassword().equals(userDto.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
}
    
