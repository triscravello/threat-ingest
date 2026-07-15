package com.threatpipeline.threatingest.controller;

import com.threatpipeline.threatingest.model.Severity;
import com.threatpipeline.threatingest.model.ThreatAlert;
import com.threatpipeline.threatingest.service.ThreatAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Threat Alerts", description = "Query enriched threat alerts with severity classifications")
public class AlertController {

    private final ThreatAnalysisService analysisService;

    public AlertController(ThreatAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping
    @Operation(summary = "Query threat alerts", description = "Returns all alerts, optionally filtered by severity")
    public List<ThreatAlert> getAlerts(@RequestParam(required = false) Severity severity) {
        return analysisService.getAlerts(severity);
    }
}