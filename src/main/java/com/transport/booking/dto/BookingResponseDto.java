package com.transport.booking.dto;

import com.transport.booking.entities.Journey;
import com.transport.booking.entities.Booking.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Integer bookingId;
    private String passengerUsername;
    private String driverUsername;
    private String source;
    private String destination;
    private LocalDate journeyDate;
    private Journey.Type transportType;
    private int seatCount;
    private LocalDateTime bookingTime;
    private Status status;
}
