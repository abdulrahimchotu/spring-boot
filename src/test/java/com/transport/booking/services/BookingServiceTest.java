package com.transport.booking.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.transport.booking.dto.BookingDto;
import com.transport.booking.dto.BookingResponseDto;
import com.transport.booking.entities.Booking;
import com.transport.booking.entities.Journey;
import com.transport.booking.entities.User;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;
import com.transport.booking.repositories.BookingRepository;
import com.transport.booking.repositories.JourneyRepository;
import com.transport.booking.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JourneyRepository journeyRepository;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;
    
    @InjectMocks
    private BookingService bookingService;

    @Test
    void testGetBookings() {
        // Create test data
        User passenger = new User();
        passenger.setUsername("testPassenger");
        
        User driver = new User();
        driver.setUsername("testDriver");
        
        Journey journey = new Journey();
        journey.setId(1);
        journey.setSource("Source");
        journey.setDestination("Destination");
        journey.setDate(LocalDate.now());
        journey.setType(Journey.Type.BUS);
        journey.setUser(driver);
        
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(passenger);
        booking.setJourney(journey);
        booking.setSeatCount(2);
        booking.setStatus(Booking.Status.BOOKED);
        booking.setBookingTime(LocalDateTime.now());
        
        // Mock repository
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        
        // Execute test
        List<BookingResponseDto> result = bookingService.getBookings();
        
        // Verify
        assertEquals(1, result.size());
        BookingResponseDto dto = result.get(0);
        assertEquals(booking.getId(), dto.getBookingId());
        assertEquals("testPassenger", dto.getPassengerUsername());
        assertEquals("testDriver", dto.getDriverUsername());
        assertEquals(journey.getSource(), dto.getSource());
        assertEquals(journey.getDestination(), dto.getDestination());
        assertEquals(booking.getSeatCount(), dto.getSeatCount());
        assertEquals(booking.getStatus(), dto.getStatus());
    }
    
    @Test
    void testGetBookingsEmpty() {
        when(bookingRepository.findAll()).thenReturn(List.of());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.getBookings();
        });
    }
    
    @Test
    void testBookJourney() {
        // Create test data
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        
        Journey journey = new Journey();
        journey.setId(1);
        journey.setAvailableSeats(10);
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setJourneyId(1);
        bookingDto.setSeatCount(2);
        
        // Mock authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(1);
        
        // Mock repositories
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(journeyRepository.findById(1)).thenReturn(Optional.of(journey));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        
        // Execute test
        bookingService.bookJourney(bookingDto);
        
        // Verify
        verify(bookingRepository).save(any(Booking.class));
        verify(journeyRepository).save(journey);
        assertEquals(8, journey.getAvailableSeats());
    }
    
    @Test
    void testBookJourneyNotEnoughSeats() {
        // Create test data
        User user = new User();
        user.setId(1);
        
        Journey journey = new Journey();
        journey.setId(1);
        journey.setAvailableSeats(1);
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setJourneyId(1);
        bookingDto.setSeatCount(2);
        
        // Mock authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(1);
        
        // Mock repositories
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(journeyRepository.findById(1)).thenReturn(Optional.of(journey));
        
        // Execute and verify
        assertThrows(BadRequestException.class, () -> {
            bookingService.bookJourney(bookingDto);
        });
    }
    
    @Test
    void testBookJourneyUserNotFound() {
        // Create test data
        BookingDto bookingDto = new BookingDto();
        bookingDto.setJourneyId(1);
        bookingDto.setSeatCount(2);
        
        // Mock authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(1);
        
        // Mock repository
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        
        // Execute and verify
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.bookJourney(bookingDto);
        });
    }
    
    @Test
    void testBookJourneyJourneyNotFound() {
        // Create test data
        User user = new User();
        user.setId(1);
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setJourneyId(1);
        bookingDto.setSeatCount(2);
        
        // Mock authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(1);
        
        // Mock repositories
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(journeyRepository.findById(1)).thenReturn(Optional.empty());
        
        // Execute and verify
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.bookJourney(bookingDto);
        });
    }
}
