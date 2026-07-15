package com.threatpipeline.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// Spring Data creates CRUD operations automatically for ThreatEventDocument
public interface ThreatEventSearchRepository extends ElasticsearchRepository<ThreatEventDocument, String> {
}