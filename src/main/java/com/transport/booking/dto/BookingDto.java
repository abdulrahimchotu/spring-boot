package com.transport.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BookingDto {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Journey ID is required")
    private Integer journeyId;

    @NotNull(message = "Seat count is required")
    @Min(value = 1, message = "Seat count must be at least 1")
    private Integer seatCount;
}

