package com.threatpipeline.threatingest.service;

import com.threatpipeline.threatingest.model.ThreatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!deploy")
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "threat-events";

    private final KafkaTemplate<String, ThreatEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, ThreatEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(ThreatEvent event) {
        kafkaTemplate.send(TOPIC, event.getId().toString(), event);
        log.info("Published event to topic: {} with id: {}", TOPIC, event.getId());
    }
}