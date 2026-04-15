package com.cruzapi.cepaudit.infra.inbound.rest;

import com.cruzapi.cepaudit.application.AuditCepUseCase;
import com.cruzapi.cepaudit.core.AuditLog;
import com.cruzapi.cepaudit.core.Cep;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ceps")
@RequiredArgsConstructor
@Tag(name = "Auditoria de CEP", description = "Endpoints para consulta e auditoria de endereços")
public class AuditCepController {
    private final AuditCepUseCase auditCepUseCase;

    @GetMapping("/{cep}")
    @Operation(summary = "Consulta um CEP", description = "Busca o endereço em uma API externa e grava o log de auditoria no banco de dados.")
    public ResponseEntity<String> auditCep(@PathVariable("cep") String rawCep) {
        Cep cep = new Cep(rawCep);

        AuditLog log = auditCepUseCase.audit(cep);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(log.payload());
    }
}
