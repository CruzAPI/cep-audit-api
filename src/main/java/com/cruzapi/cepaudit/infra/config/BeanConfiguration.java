package com.cruzapi.cepaudit.infra.config;

import com.cruzapi.cepaudit.application.AuditCepUseCase;
import com.cruzapi.cepaudit.application.AuditLogRepository;
import com.cruzapi.cepaudit.application.CepProvider;
import com.cruzapi.cepaudit.application.DefaultAuditCepUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public AuditCepUseCase auditCepUseCase(CepProvider cepProvider, AuditLogRepository repository) {
        return new DefaultAuditCepUseCase(cepProvider, repository);
    }
}
