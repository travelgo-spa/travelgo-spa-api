package com.travelgo.biblioteca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "app_users") // Evita conflicto con la tabla 'user' de H2
@Data // Para getters y setters automaticamente
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String email;
}
