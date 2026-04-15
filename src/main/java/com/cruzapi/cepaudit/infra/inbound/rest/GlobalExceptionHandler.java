package com.cruzapi.cepaudit.infra.inbound.rest;

import com.cruzapi.cepaudit.core.CepNotFoundException;
import com.cruzapi.cepaudit.core.ExternalProviderUnavailableException;
import com.cruzapi.cepaudit.core.InvalidCepFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCepFormat.class)
    public ResponseEntity<String> handleInvalidCepFormat(InvalidCepFormat ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<String> handleCepNotFound(CepNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExternalProviderUnavailableException.class)
    public ResponseEntity<String> handleExternalProviderUnavailable(ExternalProviderUnavailableException ex) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError().body("Ocorreu um erro interno no servidor.");
    }
}
