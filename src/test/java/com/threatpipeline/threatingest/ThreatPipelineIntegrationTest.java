package com.threatpipeline.threatingest;

import com.threatpipeline.threatingest.dto.CreateEventRequest;
import com.threatpipeline.threatingest.model.EventType;
import com.threatpipeline.threatingest.model.ThreatAlert;
import com.threatpipeline.threatingest.repository.ThreatAlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ThreatPipelineIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16"));

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.7.1"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ThreatAlertRepository alertRepository;

    @Test
    void fullPipeline_eventIngestionGeneratesAlert() {
        // Build a BRUTE_FORCE event request
        CreateEventRequest request = new CreateEventRequest();
        request.setSourceIp("192.168.1.50");
        request.setEventType(EventType.BRUTE_FORCE);
        request.setTargetSystem("auth-server");
        request.setRawPayload("{\"attempts\": 50}");

        // POST the event to the REST API
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/events", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Wait for Kafka consumer to process and generate an alert
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            List<ThreatAlert> alerts = alertRepository.findAll();
            assertThat(alerts).isNotEmpty();
            assertThat(alerts.get(0).getSourceIp()).isEqualTo("192.168.1.50");
        });
    }
}