package com.cruzapi.cepaudit.application;

import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultAuditCepUseCase implements AuditCepUseCase {
    private final CepProvider provider;
    private final AuditLogRepository repository;

    @Override
    public AuditLog audit(Cep cep) {
        String payload = provider.consult(cep);

        AuditLog auditLog = AuditLog.builder()
                .consultedCep(cep)
                .payload(payload)
                .build();

        return repository.save(auditLog);
    }
}
