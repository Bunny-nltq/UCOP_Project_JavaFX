package com.ucop.dao;

import com.ucop.entity.AuditLog;

public class AuditLogDAO extends GenericDAO<AuditLog> {
    public AuditLogDAO(){
        super(AuditLog.class);
    }
}
