package com.threatpipeline.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;

import java.time.Instant;

@Document(indexName = "threat-alerts")
public class ThreatAlertDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String severity;

    @Field(type = FieldType.Text)
    private String reason;

    @Field(type = FieldType.Keyword)
    private String sourceIp;

    @Field(type = FieldType.Keyword)
    private String relatedEventId;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant createdAt;

        public ThreatAlertDocument() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public String getRelatedEventId() { return relatedEventId; }
    public void setRelatedEventId(String relatedEventId) { this.relatedEventId = relatedEventId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}