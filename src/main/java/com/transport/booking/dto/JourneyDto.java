package com.transport.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JourneyDto {

    @NotBlank(message = "Source location cannot be empty")
    private String source;

    @NotBlank(message = "Destination location cannot be empty")
    private String destination;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Journey date cannot be in the past")
    private LocalDate date;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Integer price;

    @NotNull(message = "Available seats are required")
    @Min(value = 1, message = "Available seats must be greater than 0")
    private Integer availableSeats;

    @NotNull(message = "Journey type is required")
    @Pattern(regexp = "BUS|TRAIN|AIRPLANE", message = "Journey type must be either BUS, TRAIN, or AIRPLANE")
    private String type;

}
