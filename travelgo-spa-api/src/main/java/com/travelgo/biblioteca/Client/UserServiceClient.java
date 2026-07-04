package com.travelgo.biblioteca.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "travelgo-user-service", url = "${userservice.base-url}")
public interface UserServiceClient {

    /**
     * Mapea a GET {gateway}/api/users/check/{id}.
     * La llamada pasa por el API Gateway (travelgo-gateway), que enruta
     * /api/users/** hacia travelgo-user-service.
     *
     * @param id ID del usuario a verificar
     * @return true si existe, false si no
     */
    @GetMapping("/api/users/check/{id}")
    Boolean checkUserExists(@PathVariable("id") Long id);
}