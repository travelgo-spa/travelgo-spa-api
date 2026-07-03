package com.travelgo.biblioteca.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro que se ejecuta una vez por petición.
 * Extrae el token Bearer del header Authorization,
 * lo valida con JwtService y carga la autenticación en el SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", se continúa sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7); // Quita "Bearer "

        if (jwtService.isValid(token)) {
            String username = jwtService.validateAndGetUsername(token);
            log.debug("Autenticación JWT exitosa para usuario: {}", username);

            // Construye el objeto de autenticación y lo registra en el contexto
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList() // roles vacíos por ahora
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            log.warn("Token JWT inválido en petición a: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
