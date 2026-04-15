package com.cruzapi.cepaudit.application;

import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultAuditCepUseCaseTest {
    @Mock
    private CepProvider provider;

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private DefaultAuditCepUseCase useCase;

    @Test
    void shouldAuditCepSuccessfully() {
        // Arrange
        Cep cep = new Cep("01001000");
        String mockPayload = "{\"cep\": \"01001-000\", \"logradouro\": \"Praça da Sé\"}";

        AuditLog savedLog = AuditLog.builder()
                .id(UUID.randomUUID())
                .consultedCep(cep)
                .payload(mockPayload)
                .consultedAt(Instant.now())
                .build();

        when(provider.consult(cep)).thenReturn(mockPayload);
        when(repository.save(any(AuditLog.class))).thenReturn(savedLog);

        // Act
        AuditLog result = useCase.audit(cep);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedLog.id());

        verify(provider).consult(cep);

        ArgumentCaptor<AuditLog> logCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(logCaptor.capture());

        AuditLog capturedLog = logCaptor.getValue();

        // Assert
        assertThat(capturedLog.consultedCep()).isEqualTo(cep);
        assertThat(capturedLog.consultedAt())
                .isNotNull()
                .isCloseTo(savedLog.consultedAt(), within(1, ChronoUnit.SECONDS));
        assertThat(capturedLog.payload()).isEqualTo(mockPayload);
    }

    @Test
    void shouldNotSaveLogWhenProviderThrowsException() {
        // Arrange
        Cep cep = new Cep("00000000");
        when(provider.consult(cep)).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.audit(cep));

        verify(provider).consult(cep);
        verifyNoInteractions(repository);
    }
}
