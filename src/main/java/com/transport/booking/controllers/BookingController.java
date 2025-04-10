package com.transport.booking.controllers;

import com.transport.booking.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.transport.booking.entities.Booking;
import com.transport.booking.dto.BookingDto;
import com.transport.booking.services.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public List<Booking> getBookings() {
        return bookingService.getBookings();
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookJourney( @RequestBody BookingDto bookingDto) {
        bookingService.bookJourney(bookingDto);
        return ResponseEntity.ok("Journey booked successfully");
    }

    @PutMapping("/cancel/{bookingId}")
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
