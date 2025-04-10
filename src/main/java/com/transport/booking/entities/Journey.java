package com.transport.booking.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Journey {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String source;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private Integer price;
    
    @Column(nullable = false)
    private Integer availableSeats;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    
    public enum Type {
        BUS, TRAIN, AIRPLANE
    }
}
