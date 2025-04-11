package com.transport.booking.controllers;

import com.transport.booking.entities.Journey;
import com.transport.booking.services.JourneyService;
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
    public List<Journey> getJourneys() {
        return journeyService.getJourneys();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addJourney(@RequestBody Journey journey) {
        journeyService.addJourney(journey);
        return "Journey added successfully";
    }

    @GetMapping("/{type}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Journey> getJourneyByType(@PathVariable Journey.Type type) {
        return journeyService.getJourneyByType(type);
    }
}
