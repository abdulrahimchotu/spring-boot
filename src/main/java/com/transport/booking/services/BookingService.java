package com.transport.booking.services;

import com.transport.booking.repositories.BookingRepository;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;

import com.transport.booking.dto.BookingDto;
import com.transport.booking.entities.Booking;
import com.transport.booking.repositories.UserRepository;
import com.transport.booking.repositories.JourneyRepository;
import com.transport.booking.entities.User;
import com.transport.booking.entities.Journey;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final JourneyRepository journeyRepository;

    public BookingService(BookingRepository bookingRepository, 
                         UserRepository userRepository,
                         JourneyRepository journeyRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.journeyRepository = journeyRepository;
    }

    public List<Booking> getBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public void cancelBooking(Integer bookingId) {
        if (bookingId == null) {
            throw new BadRequestException("Booking ID cannot be null");
        }

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Check if booking is already cancelled
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        try {
            // Update journey available seats
            Journey journey = booking.getJourney();
            journey.setAvailableSeats(journey.getAvailableSeats() + booking.getSeatCount());
            journeyRepository.save(journey);

            // Update booking status
            booking.setStatus(Booking.Status.CANCELLED);
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new BadRequestException("Failed to cancel booking: " + e.getMessage());
        }
    }

    public void bookJourney(BookingDto bookingDto) {
        if (bookingDto.getSeatCount() <= 0) {
            throw new BadRequestException("Seat count must be greater than 0");
        }

        User user = userRepository.findById(bookingDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + bookingDto.getUserId()));

        Journey journey = journeyRepository.findById(bookingDto.getJourneyId())
            .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + bookingDto.getJourneyId()));

        if (journey.getAvailableSeats() < bookingDto.getSeatCount()) {
            throw new BadRequestException("Not enough seats available. Only " + 
                journey.getAvailableSeats() + " seats left");
        }

        Booking booking = new Booking();
        booking.setSeatCount(bookingDto.getSeatCount());
        booking.setStatus(Booking.Status.BOOKED);
        booking.setBookingTime(LocalDateTime.now());
        booking.setUser(user);
        booking.setJourney(journey);

        journey.setAvailableSeats(journey.getAvailableSeats() - bookingDto.getSeatCount());
        journeyRepository.save(journey);

        bookingRepository.save(booking);
    }
}
