package com.cruzapi.cepaudit.infra.config;

import com.cruzapi.cepaudit.infra.outbound.http.ViaCepFeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = ViaCepFeignClient.class)
public class FeignConfig {

}
