package com.travelgo.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }

    /**
     * RestTemplate para comunicación síncrona con travelgo-user-service.
     * Se usa en ReservationService para verificar que el usuario existe.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
