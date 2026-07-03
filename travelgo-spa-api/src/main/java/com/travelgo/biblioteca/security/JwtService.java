package com.travelgo.biblioteca.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final Key key;
    private final int expirationMinutes;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationMinutes:120}") int expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * Genera un token JWT firmado para el username dado.
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60L);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();

        log.info("Token JWT generado para usuario: {}", username);
        return token;
    }

    /**
     * Valida el token y retorna el username (subject).
     * Lanza JwtException si el token es inválido o expiró.
     */
    public String validateAndGetUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.debug("Token validado para usuario: {}", claims.getSubject());
        return claims.getSubject();
    }

    /**
     * Retorna true si el token es válido, false si no.
     */
    public boolean isValid(String token) {
        try {
            validateAndGetUsername(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }
}
