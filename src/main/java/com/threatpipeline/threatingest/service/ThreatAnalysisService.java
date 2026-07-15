package com.threatpipeline.threatingest.service;

import com.threatpipeline.threatingest.model.*;
import com.threatpipeline.threatingest.repository.ThreatAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class ThreatAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ThreatAnalysisService.class);
    // Escalate to CRITICAL after this many alerts from the same IP
    private static final long REPEAT_OFFENDER_THRESHOLD = 3;

    private final ThreatAlertRepository alertRepository;

    public ThreatAnalysisService(ThreatAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    // Orchestrates severity classification and alert persistence
    public ThreatAlert analyze(ThreatEvent event) {
        Severity severity = classifySeverity(event);
        String rule = determineRule(event, severity);

        ThreatAlert alert = new ThreatAlert();
        alert.setLinkedEventId(event.getId());
        alert.setSeverity(severity);
        alert.setDescription(buildDescription(event, severity));
        alert.setDetectedAt(Instant.now());
        alert.setRuleTriggered(rule);
        alert.setSourceIp(event.getSourceIp());

        ThreatAlert saved = alertRepository.save(alert);
        log.info("Generated alert: severity={}, rule={}, sourceIp={}", severity, rule, event.getSourceIp());
        return saved;
    }

    // Maps each event type to a base severity level
    private Severity classifySeverity(ThreatEvent event) {
        return switch (event.getEventType()) {
            case MALWARE_DETECTED -> Severity.CRITICAL;
            case BRUTE_FORCE -> evaluateBruteForce(event.getSourceIp());
            case PORT_SCAN -> Severity.MEDIUM;
            case FAILED_LOGIN -> Severity.LOW;
        };
    }

    // Escalates brute force to CRITICAL if IP is a repeat offender
    private Severity evaluateBruteForce(String sourceIp) {
        long priorAlerts = alertRepository.countBySourceIp(sourceIp);
        if (priorAlerts >= REPEAT_OFFENDER_THRESHOLD) {
            return Severity.CRITICAL;
        }
        return Severity.HIGH;
    }

    // Determines which named rule fired for auditing
    private String determineRule(ThreatEvent event, Severity severity) {
        if (event.getEventType() == EventType.BRUTE_FORCE && severity == Severity.CRITICAL) {
            return "REPEAT_OFFENDER_ESCALATION";
        }
        return event.getEventType().name() + "_DETECTION";
    }

    private String buildDescription(ThreatEvent event, Severity severity) {
        return String.format("%s detected from %s targeting %s",
                event.getEventType(), event.getSourceIp(), event.getTargetSystem());
    }

    // Public query method used by AlertController
    public List<ThreatAlert> getAlerts(Severity severity) {
        if (severity != null) {
            return alertRepository.findBySeverity(severity);
        }
        return alertRepository.findAll();
    }
}