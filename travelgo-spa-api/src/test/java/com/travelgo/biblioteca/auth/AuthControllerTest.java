package com.travelgo.biblioteca.auth;

import com.travelgo.biblioteca.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

    private AuthController controller;

    @BeforeEach
    void setUp() {
        JwtService jwtService = new JwtService("una-clave-secreta-de-prueba-bien-larga-para-hmac", 120);
        controller = new AuthController(jwtService);
    }

    private LoginRequest buildRequest(String username, String password) {
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }

    @Test
    void login_conCredencialesValidas_retornaTokenY200() {
        ResponseEntity<LoginResponse> response = controller.login(buildRequest("admin", "admin123"));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getToken()).isNotBlank();
    }

    @Test
    void login_conPasswordIncorrecta_retorna401() {
        ResponseEntity<LoginResponse> response = controller.login(buildRequest("admin", "incorrecta"));

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void login_conUsernameIncorrecto_retorna401() {
        ResponseEntity<LoginResponse> response = controller.login(buildRequest("otro", "admin123"));

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isNull();
    }
}
