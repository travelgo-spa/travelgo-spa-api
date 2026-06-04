package com.travelgo.biblioteca.auth;

import com.travelgo.biblioteca.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

        if (!"admin".equals(req.getUsername()) || !"admin123".equals(req.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(req.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
