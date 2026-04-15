package com.cruzapi.cepaudit.application;

import com.cruzapi.cepaudit.core.AuditLog;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
}
