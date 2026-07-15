package com.threatpipeline.threatingest.service;

import com.threatpipeline.search.ThreatEventIndexService;
import com.threatpipeline.threatingest.model.ThreatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!deploy")
public class KafkaConsumerService {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ThreatAnalysisService threatAnalysisService;
    private final ThreatEventIndexService threatEventIndexService;

    public KafkaConsumerService(
            ThreatAnalysisService threatAnalysisService,
            ThreatEventIndexService threatEventIndexService
    ) {
        this.threatAnalysisService = threatAnalysisService;
        this.threatEventIndexService = threatEventIndexService;
    }

    @KafkaListener(
            topics = "threat-events",
            groupId = "threat-analyzer-group"
    )
    public void consumeEvent(ThreatEvent event) {
        log.info("Consumed event from Kafka: {}", event.getId());

        threatAnalysisService.analyze(event);
        threatEventIndexService.index(event);

        log.info("Indexed event in Elasticsearch: {}", event.getId());
    }
}