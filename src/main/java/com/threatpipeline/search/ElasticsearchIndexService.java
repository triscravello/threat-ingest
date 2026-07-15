package com.threatpipeline.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Profile("!deploy")
public class ElasticsearchIndexService {

    private static final Logger log =
            LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final ThreatEventSearchRepository eventSearchRepository;
    private final ThreatAlertSearchRepository alertSearchRepository;

    public ElasticsearchIndexService(
            ThreatEventSearchRepository eventSearchRepository,
            ThreatAlertSearchRepository alertSearchRepository
    ) {
        this.eventSearchRepository = eventSearchRepository;
        this.alertSearchRepository = alertSearchRepository;
    }

    public void indexThreatEvent(
            UUID eventId,
            String eventType,
            String sourceIp,
            String username,
            String description,
            String processingStatus,
            Instant eventTimestamp
    ) {
        ThreatEventDocument doc = new ThreatEventDocument();

        doc.setId(eventId.toString());
        doc.setEventType(eventType);
        doc.setSourceIp(sourceIp);
        doc.setUsername(username);
        doc.setDescription(description);
        doc.setProcessingStatus(processingStatus);
        doc.setEventTimestamp(eventTimestamp);
        doc.setIndexedAt(Instant.now());

        eventSearchRepository.save(doc);

        log.info("Indexed threat event id={} into Elasticsearch", eventId);
    }

    public void indexThreatAlert(
            UUID alertId,
            String severity,
            String reason,
            String sourceIp,
            UUID relatedEventId,
            Instant createdAt
    ) {
        ThreatAlertDocument doc = new ThreatAlertDocument();

        doc.setId(alertId.toString());
        doc.setSeverity(severity);
        doc.setReason(reason);
        doc.setSourceIp(sourceIp);
        doc.setRelatedEventId(relatedEventId.toString());
        doc.setCreatedAt(createdAt);

        alertSearchRepository.save(doc);

        log.info(
                "Indexed threat alert id={} severity={} into Elasticsearch",
                alertId,
                severity
        );
    }
}