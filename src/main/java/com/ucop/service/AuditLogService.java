package com.ucop.service;

import com.ucop.dao.AuditLogDAO;
import com.ucop.entity.AuditLog;
import com.ucop.util.JsonUtil;
import com.ucop.util.SecurityContext;

import java.time.LocalDateTime;

public class AuditLogService {

    private final AuditLogDAO dao = new AuditLogDAO();

    public void log(String entityName, Long entityId, String action,
            Object oldData, Object newData, String details) {

AuditLog log = new AuditLog();

log.setEntityName(entityName);
log.setEntityId(entityId);
log.setAction(action);

log.setOldValue(JsonUtil.toJson(oldData));
log.setNewValue(JsonUtil.toJson(newData));

log.setUserId(SecurityContext.getCurrentUserId());

log.setDetails(details);
log.setCreatedAt(LocalDateTime.now());

dao.save(log);
}

}
