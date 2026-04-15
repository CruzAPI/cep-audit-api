package com.cruzapi.cepaudit.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CepTest {
    @Test
    void shouldCreateCepAndNormalizeWhenValueIsValid() {
        // Arrange & Act
        Cep cep1 = new Cep("01001000");
        Cep cep2 = new Cep("01001-000");
        Cep cep3 = new Cep("01.001-000");

        // Assert
        assertThat(cep1.value()).isEqualTo("01001000");
        assertThat(cep2.value()).isEqualTo("01001000");
        assertThat(cep3.value()).isEqualTo("01001000");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowInvalidCepFormatWhenValueIsNullOrBlank(String invalidValue) {
        assertThatThrownBy(() -> new Cep(invalidValue))
                .isInstanceOf(InvalidCepFormat.class)
                .hasMessage("O CEP não pode ser nulo ou vazio.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "123456789", "ABCDE-FGH", "01001-00A", "0A1B0C0D1E0F0G0H", "0.1.0.0.1.0.0.0"})
    void shouldThrowInvalidCepFormatWhenLengthIsInvalidOrContainsLetters(String invalidValue) {
        assertThatThrownBy(() -> new Cep(invalidValue))
                .isInstanceOf(InvalidCepFormat.class)
                .hasMessage("Formato de CEP inválido: " + invalidValue);
    }
}
