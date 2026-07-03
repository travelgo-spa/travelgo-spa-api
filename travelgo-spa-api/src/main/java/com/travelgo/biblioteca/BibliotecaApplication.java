package com.travelgo.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }
}