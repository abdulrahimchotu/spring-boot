package com.transport.booking.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.transport.booking.dto.UserDto;
import com.transport.booking.services.UserService;



@RestController
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public String getMethodName() {
        return "Hello World";
    }
    
    @PostMapping("/register")
    public String addUser(@RequestBody UserDto entity) {
        userService.addUser(entity);
        return "entity added";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto) {
        try {
            userService.login(userDto);
            return "Login successful";
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    
    
    
    
}
