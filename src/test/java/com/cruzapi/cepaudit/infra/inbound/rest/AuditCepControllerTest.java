package com.cruzapi.cepaudit.infra.inbound.rest;

import com.cruzapi.cepaudit.application.AuditCepUseCase;
import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;
import com.cruzapi.cepaudit.core.CepNotFoundException;
import com.cruzapi.cepaudit.core.ExternalProviderUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuditCepController.class, GlobalExceptionHandler.class})
class AuditCepControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditCepUseCase auditCepUseCase;

    @Test
    void shouldReturn200AndPayloadWhenCepIsValid() throws Exception {
        // Arrange
        String validRawCep = "01001000";
        String mockJsonResponse = "{\"cep\": \"01001-000\", \"logradouro\": \"Praça da Sé\"}";

        AuditLog mockLog = AuditLog.builder()
                .id(UUID.randomUUID())
                .consultedCep(new Cep(validRawCep))
                .consultedAt(Instant.now())
                .payload(mockJsonResponse)
                .build();

        when(auditCepUseCase.audit(any(Cep.class))).thenReturn(mockLog);

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", validRawCep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mockJsonResponse));
    }

    @Test
    void shouldReturn400BadRequestWhenCepFormatIsInvalid() throws Exception {
        // Arrange
        String invalidRawCep = "01001-ABC";

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", invalidRawCep))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Formato de CEP inválido: 01001-ABC"));
    }

    @Test
    void shouldReturn404NotFoundWhenCepDoesNotExist() throws Exception {
        // Arrange
        String validFormatButNonExistentCep = "01002000";

        when(auditCepUseCase.audit(any(Cep.class)))
                .thenThrow(new CepNotFoundException("O CEP 01002000 não foi encontrado."));

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", validFormatButNonExistentCep))
                .andExpect(status().isNotFound())
                .andExpect(content().string("O CEP 01002000 não foi encontrado."));
    }

    @Test
    void shouldReturn503ServiceUnavailableWhenProviderFails() throws Exception {
        // Arrange
        String validRawCep = "01001000";

        when(auditCepUseCase.audit(any(Cep.class)))
                .thenThrow(new ExternalProviderUnavailableException("Serviço de consulta de CEP indisponível no momento."));

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", validRawCep))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Serviço de consulta de CEP indisponível no momento."));
    }
}
