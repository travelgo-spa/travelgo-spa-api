package com.travelgo.promotion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @NotBlank(message = "El codigo de la promocion es obligatorio")
    private String code;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "1.0", message = "El descuento minimo es 1%")
    @DecimalMax(value = "90.0", message = "El descuento maximo es 90%")
    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "expiration_date")
    private String expirationDate;
}