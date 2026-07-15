package com.threatpipeline.threatingest.repository;

import com.threatpipeline.threatingest.model.EventType;
import com.threatpipeline.threatingest.model.ThreatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ThreatEventRepository extends JpaRepository<ThreatEvent, UUID> {
    // Spring Data generates the SQL for this query from the method name
    List<ThreatEvent> findByEventType(EventType eventType);
}