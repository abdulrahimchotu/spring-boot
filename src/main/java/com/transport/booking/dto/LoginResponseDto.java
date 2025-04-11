package com.transport.booking.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String username;
    private Boolean isAdmin;
}