package com.ucop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String entityType;
    private String entityId;
    private String action;
    private String changes;
    private String createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    public void pre() { createdAt = LocalDateTime.now(); }

    // GETTER & SETTER
    public Integer getId() { return id; }

    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public void setAction(String action) { this.action = action; }
    public void setChanges(String changes) { this.changes = changes; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
