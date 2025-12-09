package com.ucop.service;

import com.ucop.dao.AuditLogDAO;
import com.ucop.entity.AuditLog;
import com.ucop.util.JsonUtil;
import com.ucop.util.SecurityContext;

import java.time.LocalDateTime;

public class AuditLogService {

    private final AuditLogDAO dao = new AuditLogDAO();

    public void log(String entity, Long id, String action, Object oldData, Object newData) {

        AuditLog log = new AuditLog();

        log.setEntityName(entity);
        log.setEntityId(id);
        log.setAction(action);
        log.setOldValue(JsonUtil.toJson(oldData));
        log.setNewValue(JsonUtil.toJson(newData));
        log.setActor(SecurityContext.getCurrentUser());
        log.setTimestamp(LocalDateTime.now());

        dao.save(log);
    }
}
