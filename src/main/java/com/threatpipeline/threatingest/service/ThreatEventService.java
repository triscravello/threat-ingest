package com.threatpipeline.threatingest.service;

import com.threatpipeline.threatingest.dto.CreateEventRequest;
import com.threatpipeline.threatingest.model.EventType;
import com.threatpipeline.threatingest.model.ThreatEvent;
import com.threatpipeline.threatingest.repository.ThreatEventRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@Profile("!deploy")
public class ThreatEventService {

    private final ThreatEventRepository eventRepository;
    private final KafkaProducerService kafkaProducerService;

    public ThreatEventService(ThreatEventRepository eventRepository, KafkaProducerService kafkaProducerService) {
        this.eventRepository = eventRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    // Converts the request DTO into an entity and persists it
    public ThreatEvent createEvent(CreateEventRequest request) {
        ThreatEvent event = new ThreatEvent();
        event.setSourceIp(request.getSourceIp());
        event.setEventType(request.getEventType());
        event.setTargetSystem(request.getTargetSystem());
        event.setRawPayload(request.getRawPayload());

        event.setTimestamp(Instant.now());

        ThreatEvent savedEvent = eventRepository.save(event);

        kafkaProducerService.publishEvent(savedEvent);
        
        return eventRepository.save(event);
    }


    // Returns all events, or filters by type if specified
    public List<ThreatEvent> getEvents(EventType eventType) {
        if (eventType != null) {
            return eventRepository.findByEventType(eventType);
        }
        return eventRepository.findAll();
    }
}