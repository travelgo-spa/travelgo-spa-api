package com.travelgo.biblioteca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data // Para getters y setters automaticamente
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String email;
}
