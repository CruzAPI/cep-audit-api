package com.cruzapi.cepaudit.infra.outbound.http;

import com.cruzapi.cepaudit.application.CepProvider;
import com.cruzapi.cepaudit.core.Cep;
import com.cruzapi.cepaudit.core.CepNotFoundException;
import com.cruzapi.cepaudit.core.ExternalProviderUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CepProviderAdapter implements CepProvider {
    private final ViaCepFeignClient feignClient;

    @Override
    public String consult(Cep cep) {
        try {
            String payload = feignClient.getAddressByCep(cep.value());

            if (payload == null || payload.isBlank()) {
                throw new ExternalProviderUnavailableException("O provedor de CEP retornou uma resposta vazia.");
            }

            if (payload.contains("\"erro\": \"true\"")) {
                throw new CepNotFoundException("O CEP " + cep.value() + " não foi encontrado.");
            }

            return payload;
        } catch (FeignException e) {
            throw new ExternalProviderUnavailableException("Serviço de consulta de CEP indisponível no momento.");
        }
    }
}
