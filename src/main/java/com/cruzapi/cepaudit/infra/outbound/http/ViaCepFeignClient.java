package com.cruzapi.cepaudit.infra.outbound.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCepClient", url = "${api.viacep.url}")
public interface ViaCepFeignClient {
    @GetMapping("/ws/{cep}/json")
    String getAddressByCep(@PathVariable("cep") String cep);
}
