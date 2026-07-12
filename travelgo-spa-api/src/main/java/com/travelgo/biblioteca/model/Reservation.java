package com.travelgo.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "reservation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Positive(message = "El ID de usuario debe ser un número positivo")
    @Column(name = "user_id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long userId;

    // ---------- Relación JPA real hacia TravelPackage ----------
    // Antes: private Long packageId;
    // Ahora: una relación @ManyToOne de verdad, apuntando a la misma tabla de TravelPackage
    @NotNull(message = "El paquete de viaje es obligatorio")
    @ManyToOne
    @JoinColumn(name = "package_id", referencedColumnName = "id")
    private TravelPackage travelPackage;

    @NotBlank(message = "La fecha de la reserva es obligatoria")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
             message = "La fecha debe tener formato YYYY-MM-DD")
    private String date;

    public Long getPackageId() {
        return travelPackage != null ? travelPackage.getId() : null;
    }

    public void setPackageId(Long packageId) {
        if (packageId == null) {
            this.travelPackage = null;
            return;
        }
        TravelPackage ref = new TravelPackage();
        ref.setId(packageId);
        this.travelPackage = ref;
    }
}