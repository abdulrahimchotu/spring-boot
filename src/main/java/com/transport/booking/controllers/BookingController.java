package com.transport.booking.controllers;

import com.transport.booking.dto.BookingDto;
import com.transport.booking.entities.Booking;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;
import com.transport.booking.services.BookingService;

import org.springframework.http.HttpStatus;
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
    public List<Booking> getBookings() {
        return bookingService.getBookings();
    }

    @PostMapping("/book")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> bookJourney(@RequestBody BookingDto bookingDto) {
        bookingService.bookJourney(bookingDto);
        return ResponseEntity.ok("Journey booked successfully");
    }

    @PutMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> cancelBooking(@PathVariable Integer bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while cancelling the booking");
        }
    }
}
