package com.travelgo.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Positive(message = "El ID de usuario debe ser un número positivo")
    private Long userId;

    @NotNull(message = "El ID del paquete es obligatorio")
    @Positive(message = "El ID del paquete debe ser un número positivo")
    private Long packageId;

    @NotBlank(message = "La fecha de la reserva es obligatoria")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
             message = "La fecha debe tener formato YYYY-MM-DD")
    private String date;
}
