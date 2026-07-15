package com.threatpipeline.search;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@Profile("!deploy")
@EnableElasticsearchRepositories(basePackages = "com.threatpipeline.search")
public class ElasticsearchRepositoryConfig {
}