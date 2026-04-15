package com.cruzapi.cepaudit.infra.outbound.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "consulted_cep", nullable = false, length = 9)
    private String consultedCep;

    @Column(name = "consulted_at", nullable = false)
    private Instant consultedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    public AuditLogEntity(String consultedCep, Instant consultedAt, String payload) {
        this.consultedCep = consultedCep;
        this.consultedAt = consultedAt;
        this.payload = payload;
    }
}
