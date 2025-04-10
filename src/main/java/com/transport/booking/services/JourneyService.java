package com.transport.booking.services;

import org.springframework.stereotype.Service;
import com.transport.booking.repositories.JourneyRepository;
import com.transport.booking.entities.Journey;
import com.transport.booking.exceptions.BadRequestException;
import com.transport.booking.exceptions.ResourceNotFoundException;
import java.util.List;
import java.time.LocalDate;

@Service
public class JourneyService {
    private final JourneyRepository journeyRepository;

    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    public List<Journey> getJourneys() {
        List<Journey> journeys = journeyRepository.findAll();
        if (journeys.isEmpty()) {
            throw new ResourceNotFoundException("No journeys found");
        }
        return journeys;
    }

    public Journey addJourney(Journey journey) {
        if (journey.getSource() == null || journey.getSource().trim().isEmpty()) {
            throw new BadRequestException("Source location cannot be empty");
        }
        if (journey.getDestination() == null || journey.getDestination().trim().isEmpty()) {
            throw new BadRequestException("Destination location cannot be empty");
        }
        if (journey.getDate() == null) {
            throw new BadRequestException("Date cannot be empty");
        }
        if (journey.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Journey date cannot be in the past");
        }
        if (journey.getAvailableSeats() == null || journey.getAvailableSeats() <= 0) {
            throw new BadRequestException("Available seats must be greater than 0");
        }
        if (journey.getPrice() == null || journey.getPrice() <= 0) {
            throw new BadRequestException("Price must be greater than 0");
        }
        if (journey.getType() == null) {
            throw new BadRequestException("Journey type cannot be empty");
        }

        return journeyRepository.save(journey);
    }

    public List<Journey> getJourneyByType(Journey.Type type) {
        if (type == null) {
            throw new BadRequestException("Journey type cannot be null");
        }
        
        List<Journey> journeys = journeyRepository.findByType(type);
        if (journeys.isEmpty()) {
            throw new ResourceNotFoundException("No journeys found for type: " + type);
        }
        return journeys;
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
