package com.travelgo.biblioteca.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "travelgo-user-service", url = "${userservice.base-url}")
public interface UserServiceClient {

    /**
     * Mapea a GET http://localhost:8082/api/users/check/{id}
     *
     * @param id ID del usuario a verificar
     * @return true si existe, false si no
     */
    @GetMapping("/api/users/check/{id}")
    Boolean checkUserExists(@PathVariable("id") Long id);
}