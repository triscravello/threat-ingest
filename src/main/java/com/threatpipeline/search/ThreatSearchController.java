package com.threatpipeline.search;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Profile("!deploy")
@RequestMapping("/api/v1/threats")
public class ThreatSearchController {

    private final ThreatSearchService searchService;

    // Spring injects the ThreatSearchService bean
    public ThreatSearchController(ThreatSearchService searchService) {
        this.searchService = searchService;
    }

        // GET endpoint with optional query, filter, and pagination parameters
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String sourceIp,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchHits<ThreatEventDocument> hits = searchService.searchEvents(
                query, severity, eventType, sourceIp, from, to, page, size);

        // Extract document content from search hit wrappers
        List<ThreatEventDocument> results = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        // Build a response map with metadata and results
        Map<String, Object> response = new HashMap<>();
        response.put("totalHits", hits.getTotalHits());
        response.put("results", results);
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }
}