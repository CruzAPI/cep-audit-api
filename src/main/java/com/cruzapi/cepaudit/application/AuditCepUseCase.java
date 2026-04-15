package com.cruzapi.cepaudit.application;

import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;

public interface AuditCepUseCase {
    AuditLog audit(Cep cep);
}
