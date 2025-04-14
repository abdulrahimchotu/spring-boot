package com.transport.booking.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.transport.booking.repositories.JourneyRepository;
import com.transport.booking.repositories.UserRepository;
import com.transport.booking.dto.JourneyDto;
import com.transport.booking.dto.JourneyResponseDto;
import com.transport.booking.entities.Journey;
import com.transport.booking.entities.User;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;
import java.util.List;


@Service
public class JourneyService {
    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;

    public JourneyService(JourneyRepository journeyRepository, UserRepository userRepository) {
        this.journeyRepository = journeyRepository;
        this.userRepository = userRepository;
    }

    public List<JourneyResponseDto> getJourneys() {
        List<Journey> journeys = journeyRepository.findAll();
        if (journeys.isEmpty()) {
            throw new ResourceNotFoundException("No journeys found");
        }
        return journeys.stream()
            .map(j -> new JourneyResponseDto(
                j.getId(),
                j.getUser().getUsername(),
                j.getSource(),
                j.getDestination(),
                j.getDate(),
                j.getPrice(),
                j.getAvailableSeats(),
                j.getType()
            ))
            .toList();
    }

    public Journey addJourney(JourneyDto journeyDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getDetails();
    
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
        Journey journey = new Journey();
        journey.setSource(journeyDto.getSource());
        journey.setDestination(journeyDto.getDestination());
        journey.setDate(journeyDto.getDate());
        journey.setPrice(journeyDto.getPrice());
        journey.setAvailableSeats(journeyDto.getAvailableSeats());
        journey.setType(Journey.Type.valueOf(journeyDto.getType().toUpperCase()));
        journey.setUser(user);
    
        return journeyRepository.save(journey);
    }
    

    public List<JourneyResponseDto> getJourneyByType(Journey.Type type) {
        if (type == null) {
            throw new BadRequestException("Journey type cannot be null");
        }
        
        List<Journey> journeys = journeyRepository.findByType(type);
        if (journeys.isEmpty()) {
            throw new ResourceNotFoundException("No journeys found for type: " + type);
        }
        return journeys.stream()
            .map(j -> new JourneyResponseDto(
                j.getId(),
                j.getUser().getUsername(),
                j.getSource(),
                j.getDestination(),
                j.getDate(),
                j.getPrice(),
                j.getAvailableSeats(),
                j.getType()
            ))
            .toList();
    }

    public Journey getJourneyById(Integer journeyId) {
        if (journeyId == null) {
            throw new BadRequestException("Journey ID cannot be null");
        }
        return journeyRepository.findById(journeyId)
            .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + journeyId));
    }

    public void updateJourneySeats(Integer journeyId, int seatsToUpdate) {
        Journey journey = getJourneyById(journeyId);
        
        int newAvailableSeats = journey.getAvailableSeats() + seatsToUpdate;
        if (newAvailableSeats < 0) {
            throw new BadRequestException("Not enough seats available");
        }

        journey.setAvailableSeats(newAvailableSeats);
        journeyRepository.save(journey);
    }
}
