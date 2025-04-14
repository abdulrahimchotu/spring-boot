package com.transport.booking.dto;

import com.transport.booking.entities.Journey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JourneyResponseDto {
    private Long id;
    private String driverUsername;  
    private String source;
    private String destination;
    private LocalDate date;
    private Integer price;
    private Integer availableSeats;
    private Journey.Type type;
}
