package com.transport.booking.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.transport.booking.dto.JourneyDto;
import com.transport.booking.dto.JourneyResponseDto;
import com.transport.booking.entities.Journey;
import com.transport.booking.entities.User;
import com.transport.booking.exceptions.ResourceNotFoundException;
import com.transport.booking.repositories.JourneyRepository;
import com.transport.booking.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class JourneyServiceTest {
    @Mock
    JourneyRepository journeyRepository;

    @InjectMocks
    JourneyService journeyService;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContextHolder securityContextHolder;

    @Mock
    UserRepository userRepository;

    @Mock
    SecurityContext securityContext;



    @Test
    void testGetJourneys() {
        // Create a User for the Journey
        User user = new User();
        user.setUsername("testDriver");

        // Create Journey with User
        Journey journey = new Journey();
        journey.setId(1);
        journey.setSource("source");
        journey.setDestination("destination");
        journey.setDate(java.time.LocalDate.now());
        journey.setPrice(100);
        journey.setAvailableSeats(10);
        journey.setType(Journey.Type.BUS);
        journey.setUser(user);  // Set the user

        // Mocking the repository
        when(journeyRepository.findAll()).thenReturn(List.of(journey));

        // Calling the service method
        List<JourneyResponseDto> result = journeyService.getJourneys();

        // Assertions
        Assertions.assertEquals(1, result.size());
        JourneyResponseDto dto = result.get(0);
        Assertions.assertEquals(journey.getId(), dto.getId());
        Assertions.assertEquals("testDriver", dto.getDriverUsername());
        Assertions.assertEquals(journey.getSource(), dto.getSource());
        Assertions.assertEquals(journey.getDestination(), dto.getDestination());
        Assertions.assertEquals(journey.getDate(), dto.getDate());
        Assertions.assertEquals(journey.getPrice(), dto.getPrice());
        Assertions.assertEquals(journey.getAvailableSeats(), dto.getAvailableSeats());
        Assertions.assertEquals(journey.getType(), dto.getType());
    }


    @Test
    void testGetJourneysEmpty() {
        // Mocking the repository to return an empty list
        when(journeyRepository.findAll()).thenReturn(List.of());

        // Asserting that the service throws an exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            journeyService.getJourneys();
        });
    }


    @Test
    void testGetJourneysByType() {
        // Create a User for the Journey
        User user = new User();
        user.setUsername("driverforbus");

        // Create Journey with User
        Journey journey = new Journey();
        journey.setId(1);
        journey.setSource("source");
        journey.setDestination("destination");
        journey.setDate(java.time.LocalDate.now());
        journey.setPrice(100);
        journey.setAvailableSeats(10);
        journey.setType(Journey.Type.BUS);
        journey.setUser(user);  // Set the user

        // Mocking the repository
        when(journeyRepository.findByType(Journey.Type.BUS)).thenReturn(List.of(journey));

        List<JourneyResponseDto> result = journeyService.getJourneyByType(Journey.Type.BUS);

        // Assertions
        Assertions.assertEquals(1, result.size());
        JourneyResponseDto dto = result.get(0);
        Assertions.assertEquals(journey.getId(), dto.getId());
        Assertions.assertEquals("driverforbus", dto.getDriverUsername());


    }

    @Test
    void testGetJourneysByTypeEmpty() {
        // Mocking the repository to return an empty list
        when(journeyRepository.findByType(Journey.Type.BUS)).thenReturn(List.of());

        // Asserting that the service throws an exception
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            journeyService.getJourneyByType(Journey.Type.BUS);
        });
    }

    @Test
    void testGetJourneyById() {
        // Create a User for the Journey
        User user = new User();
        user.setUsername("driverforbus");

        
        Journey journey = new Journey();
        journey.setId(1);
        journey.setSource("source");
        journey.setDestination("destination");
        journey.setDate(java.time.LocalDate.now());
        journey.setPrice(100);
        journey.setAvailableSeats(10);
        journey.setType(Journey.Type.BUS);
        journey.setUser(user);  // Set the user

        // Mocking the repository
        when(journeyRepository.findById(1)).thenReturn(Optional.of(journey));

        Journey result = journeyService.getJourneyById(1);

        // Assertions
        Assertions.assertEquals(journey.getId(), result.getId());
        Assertions.assertEquals(journey.getSource(), result.getSource());
        Assertions.assertEquals(journey.getDestination(), result.getDestination());
        Assertions.assertEquals(journey.getDate(), result.getDate());
    }

    @Test 
    void testAddJourney() {         

        // Create a User for the Journey
        User user = new User();
        user.setUsername("testDriver");
        user.setId(1);

        Journey journey = new Journey();
        journey.setId(1);
        journey.setSource("source");
        journey.setDestination("destination");
        journey.setDate(java.time.LocalDate.now());
        journey.setPrice(100);
        journey.setAvailableSeats(10);
        journey.setType(Journey.Type.BUS);
        journey.setUser(user);  // Set the user

        JourneyDto journeyDto = new JourneyDto();
        journeyDto.setSource("source");
        journeyDto.setDestination("destination");
        journeyDto.setDate(java.time.LocalDate.now());
        journeyDto.setPrice(100);
        journeyDto.setAvailableSeats(10);
        journeyDto.setType("BUS");

        // Mocking the repository
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        when(journeyRepository.save(any(Journey.class))).thenReturn(journey);

        Journey addedJourney = journeyService.addJourney(journeyDto);
        
        // Assertions
        Assertions.assertEquals(journey.getId(), addedJourney.getId());
        Assertions.assertEquals(journey.getSource(), addedJourney.getSource());
        

    }

    
    
}
