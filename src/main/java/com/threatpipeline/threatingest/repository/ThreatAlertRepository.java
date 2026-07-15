package com.threatpipeline.threatingest.repository;

import com.threatpipeline.threatingest.model.Severity;
import com.threatpipeline.threatingest.model.ThreatAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ThreatAlertRepository extends JpaRepository<ThreatAlert, UUID> {
    List<ThreatAlert> findBySeverity(Severity severity);
    long countBySourceIp(String sourceIp);
}