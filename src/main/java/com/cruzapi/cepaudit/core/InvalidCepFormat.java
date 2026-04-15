package com.cruzapi.cepaudit.core;

public class InvalidCepFormat extends IllegalArgumentException {
    public InvalidCepFormat(String message) {
        super(message);
    }
}
