package com.transport.booking.services;

import com.transport.booking.repositories.BookingRepository;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;

import com.transport.booking.dto.BookingDto;
import com.transport.booking.dto.BookingResponseDto;
import com.transport.booking.entities.Booking;
import com.transport.booking.repositories.UserRepository;
import com.transport.booking.repositories.JourneyRepository;
import com.transport.booking.entities.User;
import com.transport.booking.entities.Journey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<BookingResponseDto> getBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found.");
        }

        return bookings.stream().map(booking -> {
            Journey journey = booking.getJourney();
            User passenger = booking.getUser();
            User driver = journey.getUser();

            return new BookingResponseDto(
                booking.getId(),
                passenger.getUsername(),
                driver.getUsername(),
                journey.getSource(),
                journey.getDestination(),
                journey.getDate(),
                journey.getType(),
                booking.getSeatCount(),
                booking.getBookingTime(),
                booking.getStatus()
                );
            }).collect(Collectors.toList());
        }


    public void bookJourney(BookingDto bookingDto) {
        if (bookingDto.getSeatCount() <= 0) {
            throw new BadRequestException("Seat count must be greater than 0");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getDetails();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

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
