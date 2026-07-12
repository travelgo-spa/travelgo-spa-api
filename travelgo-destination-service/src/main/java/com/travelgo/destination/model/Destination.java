package com.travelgo.destination.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "destination")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @NotBlank(message = "El nombre del destino es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El pais es obligatorio")
    private String country;

    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    private String description;
}