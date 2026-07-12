package com.travelgo.wishlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "wishlist_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Positive(message = "El ID de usuario debe ser un numero positivo")
    @Column(name = "user_id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long userId;

    @NotNull(message = "El ID del paquete es obligatorio")
    @Positive(message = "El ID del paquete debe ser un numero positivo")
    @Column(name = "package_id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long packageId;

    @NotBlank(message = "La fecha de agregado es obligatoria")
    @Column(name = "added_at")
    private String addedAt;
}