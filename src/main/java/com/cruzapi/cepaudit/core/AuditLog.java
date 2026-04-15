package com.cruzapi.cepaudit.core;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record AuditLog(UUID id, Cep consultedCep, Instant consultedAt, String payload) {
    @Builder
    public AuditLog(UUID id, Cep consultedCep, Instant consultedAt, String payload) {
        this.id = id;
        this.consultedCep = requireNonNull(consultedCep);
        this.consultedAt = consultedAt == null ? Instant.now() : consultedAt;
        this.payload = requireNonNull(payload);
    }
}
