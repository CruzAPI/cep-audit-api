package com.cruzapi.cepaudit.application;

import com.cruzapi.cepaudit.core.Cep;

public interface CepProvider {
    String consult(Cep cep);
}
