package com.cruzapi.cepaudit.infra.inbound.rest;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "api.viacep.url=http://localhost:${wiremock.server.port}"
})
@Transactional
class AuditCepControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200AndSaveToDatabaseWhenCepIsValidE2E() throws Exception {
        // Arrange
        String validCep = "01001000";
        String expectedViaCepResponse = """
                {
                  "cep": "01001-000",
                  "logradouro": "Praça da Sé",
                  "localidade": "São Paulo",
                  "uf": "SP"
                }
                """;

        stubFor(WireMock.get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedViaCepResponse)));

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", validCep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedViaCepResponse));
    }

    @Test
    void shouldReturn404WhenCepIsNotFoundInViaCepE2E() throws Exception {
        // Arrange
        String notFoundCep = "01002000";
        String viaCepErrorResponse = "{ \"erro\": \"true\" }";

        stubFor(WireMock.get(urlEqualTo("/ws/01002000/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(viaCepErrorResponse)));

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", notFoundCep))
                .andExpect(status().isNotFound())
                .andExpect(content().string("O CEP 01002000 não foi encontrado."));
    }

    @Test
    void shouldReturn503WhenViaCepIsDownE2E() throws Exception {
        // Arrange
        String validCep = "01001000";

        stubFor(WireMock.get(urlEqualTo("/ws/01001000/json"))
                .willReturn(aResponse().withStatus(500)));

        // Act & Assert
        mockMvc.perform(get("/v1/ceps/{cep}", validCep))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Serviço de consulta de CEP indisponível no momento."));
    }
}
