package com.threatpipeline.threatingest.dto;

import com.threatpipeline.threatingest.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateEventRequest {

    @NotBlank(message = "sourceIp is required")
    private String sourceIp;

    @NotNull(message = "eventType is required")
    private EventType eventType;

    @NotBlank(message = "targetSystem is required")
    private String targetSystem;

    private String rawPayload;

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public String getTargetSystem() { return targetSystem; }
    public void setTargetSystem(String targetSystem) { this.targetSystem = targetSystem; }
    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }
}