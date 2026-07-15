package com.threatpipeline.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = com.threatpipeline.threatingest.ThreatIngestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ThreatSearchIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
            "docker.elastic.co/elasticsearch/elasticsearch:8.18.7-amd64")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("discovery.type", "single-node")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    }

    @Autowired
    private ThreatEventSearchRepository eventSearchRepository;

    @Autowired
    private ThreatSearchService searchService;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setUp() {
        eventSearchRepository.deleteAll();
    }

    @Test
    void shouldIndexAndSearchByKeyword() {
        ThreatEventDocument doc = new ThreatEventDocument();
        doc.setId("1");
        doc.setEventType("LOGIN_FAILURE");
        doc.setSourceIp("192.168.1.100");
        doc.setUsername("admin");
        doc.setDescription("Brute force login attempt detected");
        doc.setProcessingStatus("PROCESSED");
        doc.setEventTimestamp(Instant.now());
        doc.setIndexedAt(Instant.now());
        eventSearchRepository.save(doc);

        // Elasticsearch needs a brief refresh
        elasticsearchOperations.indexOps(ThreatEventDocument.class).refresh();

        SearchHits<ThreatEventDocument> results = searchService.searchEvents(
                "brute force", null, null, null, null, null, 0, 10);

        assertThat(results.getTotalHits()).isGreaterThanOrEqualTo(1);
        assertThat(results.getSearchHits().get(0).getContent().getDescription())
                .contains("Brute force");
    }

    @Test
    void shouldFilterBySeverity() {
        ThreatEventDocument doc1 = new ThreatEventDocument();
        doc1.setId("2");
        doc1.setEventType("PORT_SCAN");
        doc1.setSourceIp("10.0.0.50");
        doc1.setUsername("scanner");
        doc1.setDescription("Port scanning activity");
        doc1.setProcessingStatus("HIGH");
        doc1.setEventTimestamp(Instant.now());
        doc1.setIndexedAt(Instant.now());

        ThreatEventDocument doc2 = new ThreatEventDocument();
        doc2.setId("3");
        doc2.setEventType("INFO_DISCLOSURE");
        doc2.setSourceIp("10.0.0.51");
        doc2.setUsername("user1");
        doc2.setDescription("Information disclosure attempt");
        doc2.setProcessingStatus("LOW");
        doc2.setEventTimestamp(Instant.now());
        doc2.setIndexedAt(Instant.now());
        eventSearchRepository.save(doc1);
        eventSearchRepository.save(doc2);
        elasticsearchOperations.indexOps(ThreatEventDocument.class).refresh();

        SearchHits<ThreatEventDocument> results = searchService.searchEvents(
                null, "HIGH", null, null, null, null, 0, 10);

        assertThat(results.getTotalHits()).isEqualTo(1);
        assertThat(results.getSearchHits().get(0).getContent().getSourceIp())
                .isEqualTo("10.0.0.50");
    }
}