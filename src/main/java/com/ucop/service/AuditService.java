package com.ucop.service;

import com.ucop.dao.AuditLogDAO;
import com.ucop.entity.AuditLog;

public class AuditService {

    private final AuditLogDAO dao = new AuditLogDAO();

    public void save(AuditLog a){
        dao.save(a);
    }
}
