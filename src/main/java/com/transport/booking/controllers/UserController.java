package com.transport.booking.controllers;

import com.transport.booking.dto.UserDto;
import com.transport.booking.dto.LoginResponseDto;
import com.transport.booking.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDto userDto) {
        LoginResponseDto response = userService.login(userDto);
        return ResponseEntity.ok(response);
    }
}
