package com.threatpipeline.threatingest.service;

import com.threatpipeline.threatingest.model.*;
import com.threatpipeline.threatingest.repository.ThreatAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreatAnalysisServiceTest {

    @Mock
    private ThreatAlertRepository alertRepository;

    private ThreatAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new ThreatAnalysisService(alertRepository);

        when(alertRepository.save(any(ThreatAlert.class))).thenAnswer(invocation -> {
            ThreatAlert alert = invocation.getArgument(0);
            alert.setId(UUID.randomUUID());
            return alert;
        });
    }

    @Test
    void analyzeMalwareDetected_returnsCritical() {
        ThreatEvent event = createEvent(EventType.MALWARE_DETECTED);

        ThreatAlert alert = analysisService.analyze(event);

        assertThat(alert.getSeverity()).isEqualTo(Severity.CRITICAL);
        assertThat(alert.getRuleTriggered()).isEqualTo("MALWARE_DETECTED_DETECTION");
    }

    @Test
    void analyzeBruteForce_returnsHigh() {
        when(alertRepository.countBySourceIp("10.0.0.1")).thenReturn(0L);
        ThreatEvent event = createEvent(EventType.BRUTE_FORCE);

        ThreatAlert alert = analysisService.analyze(event);

        assertThat(alert.getSeverity()).isEqualTo(Severity.HIGH);
    }

    @Test
    void analyzeBruteForceRepeatOffender_returnsCritical() {
        when(alertRepository.countBySourceIp("10.0.0.1")).thenReturn(3L);
        ThreatEvent event = createEvent(EventType.BRUTE_FORCE);

        ThreatAlert alert = analysisService.analyze(event);

        assertThat(alert.getSeverity()).isEqualTo(Severity.CRITICAL);
        assertThat(alert.getRuleTriggered()).isEqualTo("REPEAT_OFFENDER_ESCALATION");
    }

    @Test
    void analyzePortScan_returnsMedium() {
        ThreatEvent event = createEvent(EventType.PORT_SCAN);

        ThreatAlert alert = analysisService.analyze(event);

        assertThat(alert.getSeverity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    void analyzeFailedLogin_returnsLow() {
        ThreatEvent event = createEvent(EventType.FAILED_LOGIN);

        ThreatAlert alert = analysisService.analyze(event);

        assertThat(alert.getSeverity()).isEqualTo(Severity.LOW);
    }

    // Helper to create a test event with common defaults
    private ThreatEvent createEvent(EventType type) {
        ThreatEvent event = new ThreatEvent();
        event.setId(UUID.randomUUID());
        event.setSourceIp("10.0.0.1");
        event.setEventType(type);
        event.setTargetSystem("prod-server-01");
        event.setTimestamp(Instant.now());
        return event;
    }
}