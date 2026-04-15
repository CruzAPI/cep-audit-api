package com.cruzapi.cepaudit.infra.outbound.persistence;

import com.cruzapi.cepaudit.application.AuditLogRepository;
import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepository {
    private final AuditLogJpaRepository repository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = AuditLogEntity.builder()
                .consultedAt(auditLog.consultedAt())
                .consultedCep(auditLog.consultedCep().value())
                .payload(auditLog.payload())
                .build();

        AuditLogEntity savedEntity = repository.save(entity);

        return AuditLog.builder()
                .id(savedEntity.getId())
                .consultedAt(savedEntity.getConsultedAt())
                .consultedCep(new Cep(savedEntity.getConsultedCep()))
                .payload(savedEntity.getPayload())
                .build();
    }
}
