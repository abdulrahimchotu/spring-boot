package com.transport.booking.controllers;

import com.transport.booking.dto.BookingDto;
import com.transport.booking.dto.BookingResponseDto;
import com.transport.booking.services.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponseDto> getBookings() {
        return bookingService.getBookings();
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> bookJourney(@Valid @RequestBody BookingDto bookingDto) {
        bookingService.bookJourney(bookingDto);
        return ResponseEntity.ok("Journey booked successfully");
    }

    

}
