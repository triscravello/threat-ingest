package com.threatpipeline.search;

import com.threatpipeline.threatingest.model.ThreatEvent;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.time.Instant;

@Service
@Profile("!deploy")
public class ThreatEventIndexService {

    private final ThreatEventSearchRepository repository;

    public ThreatEventIndexService(ThreatEventSearchRepository repository) {
        this.repository = repository;
    }

    public ThreatEventDocument index(ThreatEvent event) {
        ThreatEventDocument document = new ThreatEventDocument();

        document.setId(event.getId().toString());
        document.setEventType(event.getEventType().name());
        document.setSourceIp(event.getSourceIp());

        // Temporary mappings based on the fields your JPA entity currently has
        document.setUsername(null);
        document.setDescription(event.getRawPayload());
        document.setProcessingStatus("PROCESSED");

        document.setEventTimestamp(event.getTimestamp());
        document.setIndexedAt(Instant.now());

        return repository.save(document);
    }
}