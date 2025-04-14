package com.transport.booking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    
    @NotNull(message = "Journey ID cannot be null")
    private Integer journeyId;

    @NotNull(message = "Seat count cannot be null")
    @Positive(message = "Seat count must be greater than zero")
    @Min(value = 1, message = "Seat count must be at least 1")
    private Integer seatCount;
}
