package com.threatpipeline.threatingest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.threatpipeline")
public class ThreatIngestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreatIngestApplication.class, args);
    }
}

