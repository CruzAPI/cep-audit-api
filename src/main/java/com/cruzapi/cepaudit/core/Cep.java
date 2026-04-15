package com.cruzapi.cepaudit.core;

public record Cep(String value) {
    private static final String CEP_PATTERN = "^(\\d{8}|\\d{5}-\\d{3}|\\d{2}\\.\\d{3}-\\d{3})$";

    public Cep {
        if (value == null || value.isBlank()) {
            throw new InvalidCepFormat("O CEP não pode ser nulo ou vazio.");
        }

        if (!value.matches(CEP_PATTERN)) {
            throw new InvalidCepFormat("Formato de CEP inválido: " + value);
        }

        value = value.replaceAll("[.\\-]", "");
    }
}
