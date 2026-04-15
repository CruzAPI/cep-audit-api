package com.cruzapi.cepaudit.infra.outbound.persistence;

import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import(AuditLogRepositoryAdapter.class)
class AuditLogRepositoryAdapterTest {
    @Autowired
    private AuditLogRepositoryAdapter adapter;

    @Autowired
    private AuditLogJpaRepository repository;

    @Test
    void shouldSaveAndMapAuditLogSuccessfully() {
        // Arrange
        Cep cep = new Cep("01001000");
        String payload = "{\"cep\": \"01001-000\", \"logradouro\": \"Praça da Sé\"}";
        Instant now = Instant.now();

        AuditLog logToSave = AuditLog.builder()
                .consultedCep(cep)
                .consultedAt(now)
                .payload(payload)
                .build();

        // Act
        AuditLog savedLog = adapter.save(logToSave);

        // Assert
        assertThat(savedLog).isNotNull();
        assertThat(savedLog.id()).isNotNull();
        assertThat(savedLog.consultedCep()).isEqualTo(cep);
        assertThat(savedLog.payload()).isEqualTo(payload);
        assertThat(savedLog.consultedAt()).isEqualTo(now);

        AuditLogEntity entityInDb = repository.findById(savedLog.id()).orElse(null);

        assertThat(entityInDb).isNotNull();
        assertThat(entityInDb.getId()).isEqualTo(savedLog.id());
        assertThat(entityInDb.getConsultedCep()).isEqualTo(cep.value());
        assertThat(entityInDb.getPayload()).isEqualTo(payload);
        assertThat(entityInDb.getConsultedAt()).isEqualTo(now);
    }

    @Test
    void shouldThrowExceptionWhenAuditLogIsNull() {
        assertThatThrownBy(() -> adapter.save(null)).isInstanceOf(NullPointerException.class);
    }
}
