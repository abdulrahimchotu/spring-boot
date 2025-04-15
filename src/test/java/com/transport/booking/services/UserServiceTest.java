package com.transport.booking.services;

import com.transport.booking.dto.UserDto;
import com.transport.booking.dto.UserLoginDto;
import com.transport.booking.dto.LoginResponseDto;
import com.transport.booking.entities.User;
import com.transport.booking.exceptions.AuthenticationException;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.repositories.UserRepository;
import com.transport.booking.util.JwtUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserService userService;

    // Register tests
    @Test
    void testRegister() {
        // Creating test data
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        userDto.setPassword("test");
        userDto.setEmail("test@gmail.com");
        userDto.setRole("USER");

        // Creating the User entity
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRole(User.UserRole.valueOf(userDto.getRole().toUpperCase()));

        // Mocking the repository
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the service method
        User addedUser = userService.register(userDto);

        // Asserting the returned user fields
        Assertions.assertNotNull(addedUser);
        Assertions.assertEquals(userDto.getUsername(), addedUser.getUsername());
        Assertions.assertEquals(userDto.getPassword(), addedUser.getPassword());
        Assertions.assertEquals(userDto.getEmail(), addedUser.getEmail());
        Assertions.assertEquals(User.UserRole.valueOf(userDto.getRole().toUpperCase()), addedUser.getRole());
    }

    @Test
    void testRegisterUsernameAlreadyExists() {
        // Creating test data
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        userDto.setPassword("test");
        userDto.setEmail("test@gmail.com");
        userDto.setRole("USER");

        // Mocking the repository to simulate existing username
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(new User()));

        // Asserting that the service throws an exception
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(userDto);
        });
    }

    // Login tests
    @Test
    void testLoginSuccess() {
        // Creating test login data
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("test");
        userLoginDto.setPassword("test");

        // Creating a mock user for the successful login scenario
        User user = new User();
        user.setUsername(userLoginDto.getUsername());
        user.setPassword(userLoginDto.getPassword());
        user.setRole(User.UserRole.USER);

        // Mocking the repository to return the user
        when(userRepository.findByUsername(userLoginDto.getUsername())).thenReturn(Optional.of(user));

        // Mocking the JwtUtil to return a token
        String token = "mocked-token";
        when(jwtUtil.generateToken(any(Map.class), eq(user.getUsername()))).thenReturn(token);

        // Calling the login method
        LoginResponseDto response = userService.login(userLoginDto);

        // Asserting that the returned response is correct
        Assertions.assertNotNull(response);
        Assertions.assertEquals(token, response.getToken()); 
        Assertions.assertEquals(user.getUsername(), response.getUsername()); 
        Assertions.assertEquals(user.getRole().toString(), response.getRole()); 
    }


    @Test
    void testLoginInvalidPassword() {
        // Creating test login data
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("test");
        userLoginDto.setPassword("wrong-password");

        // Creating a mock user for the login scenario
        User user = new User();
        user.setUsername(userLoginDto.getUsername());
        user.setPassword("correct-password");

        // Mocking the repository to return the user
        when(userRepository.findByUsername(userLoginDto.getUsername())).thenReturn(Optional.of(user));

        // Asserting that the service throws an exception due to invalid password
        Assertions.assertThrows(AuthenticationException.class, () -> {
            userService.login(userLoginDto);
        });
    }

    @Test
    void testLoginInvalidUsername() {
        // Creating test login data
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("nonexistent-user");
        userLoginDto.setPassword("password");

        // Mocking the repository to return an empty Optional
        when(userRepository.findByUsername(userLoginDto.getUsername())).thenReturn(Optional.empty());

        // Asserting that the service throws an exception due to invalid username
        Assertions.assertThrows(AuthenticationException.class, () -> {
            userService.login(userLoginDto);
        });
    }

    @Test
    void testLoginEmptyCredentials() {
        // Creating test login data with empty credentials
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("");
        userLoginDto.setPassword("");

        // Asserting that the service throws an exception for empty username or password
        Assertions.assertThrows(AuthenticationException.class, () -> {
            userService.login(userLoginDto);
        });
    }
}
