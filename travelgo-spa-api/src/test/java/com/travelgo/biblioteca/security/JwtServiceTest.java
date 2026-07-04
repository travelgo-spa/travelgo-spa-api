package com.travelgo.biblioteca.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("una-clave-secreta-de-prueba-bien-larga-para-hmac", 120);
    }

    @Test
    void generateToken_retornaUnTokenNoVacio() {
        String token = jwtService.generateToken("admin");

        assertThat(token).isNotBlank();
    }

    @Test
    void validateAndGetUsername_conTokenValido_retornaElUsername() {
        String token = jwtService.generateToken("admin");

        String username = jwtService.validateAndGetUsername(token);

        assertThat(username).isEqualTo("admin");
    }

    @Test
    void isValid_conTokenValido_retornaTrue() {
        String token = jwtService.generateToken("admin");

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void isValid_conTokenMalformado_retornaFalse() {
        assertThat(jwtService.isValid("esto-no-es-un-jwt")).isFalse();
    }

    @Test
    void isValid_conTokenFirmadoConOtraClave_retornaFalse() {
        JwtService otroServicio = new JwtService("otra-clave-secreta-completamente-distinta-hmac", 120);
        String token = otroServicio.generateToken("admin");

        assertThat(jwtService.isValid(token)).isFalse();
    }
}
