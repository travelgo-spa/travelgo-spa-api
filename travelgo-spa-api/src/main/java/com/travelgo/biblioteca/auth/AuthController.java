package com.travelgo.biblioteca.auth;

import com.travelgo.biblioteca.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint de autenticación.
 * POST /auth/login → retorna JWT si las credenciales son válidas.
 *
 * Nota: en producción real, las credenciales se validan contra la base de datos.
 * Para este proyecto, se usa un usuario "admin" hardcodeado por simplicidad.
 */

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints para iniciar sesión y obtener el token JWT")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("Intento de login para usuario: '{}'", req.getUsername());

        // Validación de credenciales (hardcoded para el proyecto semestral)
        if (!"admin".equals(req.getUsername()) || !"admin123".equals(req.getPassword())) {
            log.warn("Login fallido para usuario: '{}'", req.getUsername());
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(req.getUsername());
        log.info("Login exitoso para usuario: '{}'", req.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
