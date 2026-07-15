package com.threatpipeline.threatingest.controller;

import com.threatpipeline.threatingest.dto.CreateEventRequest;
import com.threatpipeline.threatingest.model.EventType;
import com.threatpipeline.threatingest.model.ThreatEvent;
import com.threatpipeline.threatingest.service.ThreatEventService;
import org.springframework.context.annotation.Profile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Profile("!deploy")
@RequestMapping("/api/events")
@Tag(name = "Threat Events", description = "Ingest and query raw security events")
public class ThreatEventController {

    private final ThreatEventService eventService;

    public ThreatEventController(ThreatEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ingest a security event", description = "Validates, persists, and publishes the event to Kafka")
    @ApiResponse(responseCode = "201", description = "Event created and published")
    public ThreatEvent createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping
    @Operation(summary = "Query threat events", description = "Returns all events, optionally filtered by type")
    public List<ThreatEvent> getEvents(@RequestParam(required = false) EventType eventType) {
        return eventService.getEvents(eventType);
    }
}