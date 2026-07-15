package com.threatpipeline.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.List;

@Service
@Profile("!deploy")
public class ThreatSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final Timer searchTimer;

    // Registers a custom Micrometer timer to track ES query duration
    public ThreatSearchService(ElasticsearchOperations elasticsearchOperations,
                                MeterRegistry meterRegistry) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.searchTimer = Timer.builder("threat.search.query.duration")
                .description("Time taken to execute Elasticsearch search queries")
                .register(meterRegistry);
    }

        // Builds a dynamic bool query combining full-text search with filters
    public SearchHits<ThreatEventDocument> searchEvents(String query, String severity,
                                                         String eventType, String sourceIp,
                                                         Instant from, Instant to,
                                                         int page, int size) {
        return searchTimer.record(() -> {
            BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

            // Full-text search across multiple fields
            if (query != null && !query.isBlank()) {
                boolBuilder.must(Query.of(q -> q.multiMatch(m -> m
                        .query(query)
                        .fields("description", "username", "eventType", "sourceIp")
                )));
            }

            // Exact-match filter on processingStatus for severity
            if (severity != null && !severity.isBlank()) {
                boolBuilder.filter(Query.of(q -> q.term(t -> t
                        .field("processingStatus")
                        .value(severity)
                )));
            }

            // Text match filter for event type
            if (eventType != null && !eventType.isBlank()) {
                boolBuilder.filter(Query.of(q -> q.match(m -> m
                        .field("eventType")
                        .query(eventType)
                )));
            }

                        // Exact-match filter for source IP address
            if (sourceIp != null && !sourceIp.isBlank()) {
                boolBuilder.filter(Query.of(q -> q.term(t -> t
                        .field("sourceIp")
                        .value(sourceIp)
                )));
            }

            // Date range filter on eventTimestamp
            if (from != null || to != null) {
                boolBuilder.filter(Query.of(q -> q.range(r -> r
                        .date(d -> {
                            d.field("eventTimestamp");
                            if (from != null) d.gte(from.toString());
                            if (to != null) d.lte(to.toString());
                            return d;
                        })
                )));
            }

            // Build and execute the paginated query
            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(Query.of(q -> q.bool(boolBuilder.build())))
                    .withPageable(PageRequest.of(page, size))
                    .build();

            return elasticsearchOperations.search(searchQuery, ThreatEventDocument.class);
        });
    }
}