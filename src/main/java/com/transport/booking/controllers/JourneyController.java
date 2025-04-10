package com.transport.booking.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.transport.booking.entities.Journey;
import com.transport.booking.services.JourneyService;


@RestController
@RequestMapping("/journey")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/all")
    public List<Journey> getJourneys() {
        return journeyService.getJourneys();
    }

    @PostMapping("/add")
    public String addJourney(@RequestBody Journey journey) {
        journeyService.addJourney(journey);
        return "Journey added";
    }

    @GetMapping("/{type}")
    public List<Journey> getJourneyByType(@PathVariable Journey.Type type) {
        return journeyService.getJourneyByType(type);
    }
}
