package com.threatpipeline.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// Spring Data creates CRUD operations automatically for ThreatAlertDocument
public interface ThreatAlertSearchRepository extends ElasticsearchRepository<ThreatAlertDocument, String> {
}