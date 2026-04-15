package com.cruzapi.cepaudit.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class AuditLogTest {
    @Test
    void shouldCreateAuditLogSuccessfullyWhenAllDataIsProvided() {
        // Arrange
        UUID id = UUID.randomUUID();
        Cep cep = new Cep("01001000");
        Instant now = Instant.now();
        String payload = "{\"logradouro\": \"Praça da Sé\"}";

        // Act
        AuditLog log = AuditLog.builder()
                .id(id)
                .consultedCep(cep)
                .consultedAt(now)
                .payload(payload)
                .build();

        // Assert
        assertThat(log.id()).isEqualTo(id);
        assertThat(log.consultedCep()).isEqualTo(cep);
        assertThat(log.consultedAt()).isEqualTo(now);
        assertThat(log.payload()).isEqualTo(payload);
    }

    @Test
    void shouldGenerateConsultedAtWhenNotProvided() {
        // Arrange
        Cep cep = new Cep("01001000");
        String payload = "{}";

        // Act
        AuditLog log = AuditLog.builder()
                .consultedCep(cep)
                .payload(payload)
                .build();

        // Assert
        assertThat(log.consultedAt())
                .isNotNull()
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenRequiredFieldsAreNull() {
        assertThatThrownBy(() -> AuditLog.builder()
                .consultedCep(null)
                .payload("{}")
                .build())
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> AuditLog.builder()
                .consultedCep(new Cep("01001000"))
                .payload(null)
                .build())
                .isInstanceOf(NullPointerException.class);
    }
}
