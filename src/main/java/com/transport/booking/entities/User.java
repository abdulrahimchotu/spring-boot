package com.transport.booking.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users") 
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    public static enum UserRole {
        USER, ADMIN, DRIVER
    }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
}
