package com.transport.booking.controllers;

import com.transport.booking.dto.JourneyDto;
import com.transport.booking.dto.JourneyResponseDto;
import com.transport.booking.entities.Journey;
import com.transport.booking.services.JourneyService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journeys")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<JourneyResponseDto> getJourneys() {
        return journeyService.getJourneys();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','DRIVER')")
    public ResponseEntity<String> addJourney(@Valid @RequestBody JourneyDto journey) {
        journeyService.addJourney(journey);
        return ResponseEntity.ok("Journey added successfully");
    }

    @GetMapping("/{type}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<JourneyResponseDto> getJourneyByType(@PathVariable Journey.Type type) {
        return journeyService.getJourneyByType(type);
    }
}
