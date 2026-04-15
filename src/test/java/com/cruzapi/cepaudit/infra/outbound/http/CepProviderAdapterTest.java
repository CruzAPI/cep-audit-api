package com.cruzapi.cepaudit.infra.outbound.http;

import com.cruzapi.cepaudit.core.Cep;
import com.cruzapi.cepaudit.core.CepNotFoundException;
import com.cruzapi.cepaudit.core.ExternalProviderUnavailableException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CepProviderAdapterTest {
    @Mock
    private ViaCepFeignClient feignClient;

    @InjectMocks
    private CepProviderAdapter adapter;

    @Test
    void shouldReturnPayloadWhenConsultIsSuccessful() {
        // Arrange
        Cep cep = new Cep("01001000");
        String expectedPayload = "{\"cep\": \"01001-000\", \"logradouro\": \"Praça da Sé\"}";
        when(feignClient.getAddressByCep(cep.value())).thenReturn(expectedPayload);

        // Act
        String result = adapter.consult(cep);

        // Assert
        assertThat(result).isEqualTo(expectedPayload);
        verify(feignClient).getAddressByCep(cep.value());
    }

    @Test
    void shouldThrowExternalProviderUnavailableExceptionWhenPayloadIsNull() {
        // Arrange
        Cep cep = new Cep("01001000");
        when(feignClient.getAddressByCep(cep.value())).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> adapter.consult(cep))
                .isInstanceOf(ExternalProviderUnavailableException.class)
                .hasMessage("O provedor de CEP retornou uma resposta vazia.");
    }

    @Test
    void shouldThrowExternalProviderUnavailableExceptionWhenPayloadIsBlank() {
        // Arrange
        Cep cep = new Cep("01001000");
        when(feignClient.getAddressByCep(cep.value())).thenReturn("   ");

        // Act & Assert
        assertThatThrownBy(() -> adapter.consult(cep))
                .isInstanceOf(ExternalProviderUnavailableException.class)
                .hasMessage("O provedor de CEP retornou uma resposta vazia.");
    }

    @Test
    void shouldThrowCepNotFoundExceptionWhenPayloadContainsErro() {
        // Arrange
        Cep cep = new Cep("01002000");
        String errorPayload = "{ \"erro\": \"true\" }";
        when(feignClient.getAddressByCep(cep.value())).thenReturn(errorPayload);

        // Act & Assert
        assertThatThrownBy(() -> adapter.consult(cep))
                .isInstanceOf(CepNotFoundException.class)
                .hasMessage("O CEP 01002000 não foi encontrado.");
    }

    @Test
    void shouldThrowExternalProviderUnavailableExceptionWhenFeignThrowsException() {
        // Arrange
        Cep cep = new Cep("01001000");

        FeignException feignExceptionMock = mock(FeignException.class);
        when(feignClient.getAddressByCep(cep.value())).thenThrow(feignExceptionMock);

        // Act & Assert
        assertThatThrownBy(() -> adapter.consult(cep))
                .isInstanceOf(ExternalProviderUnavailableException.class)
                .hasMessage("Serviço de consulta de CEP indisponível no momento.");
    }
}
