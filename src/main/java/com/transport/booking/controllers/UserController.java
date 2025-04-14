package com.transport.booking.controllers;

import com.transport.booking.dto.UserDto;
import com.transport.booking.dto.UserLoginDto;
import com.transport.booking.dto.LoginResponseDto;
import com.transport.booking.services.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
        userService.register(userDto);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody UserLoginDto userDto) {
        LoginResponseDto response = userService.login(userDto);
        return ResponseEntity.ok(response);
    }
}
