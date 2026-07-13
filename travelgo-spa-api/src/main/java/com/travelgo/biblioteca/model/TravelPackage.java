package com.travelgo.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "travel_package")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @NotBlank(message = "El nombre del paquete es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double price;

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración mínima es 1 día")
    @Max(value = 365, message = "La duración máxima es 365 días")
    private Integer durationDays;

    @ElementCollection
    @CollectionTable(name = "travel_package_destinations",
                     joinColumns = @JoinColumn(name = "travel_package_id"))
    @Column(name = "destinations")
    private List<String> destinations;

    // ---------- Lado inverso de la relación (@OneToMany) ----------
    // Reservation ya tiene el lado @ManyToOne (dueño de la relación, con la FK).
    // Este es el lado "muchos", solo de lectura: permite navegar desde un
    // paquete hacia todas sus reservas, sin agregar una columna nueva
    // (mappedBy indica que la FK ya vive en la tabla Reservation).
    //
    // @JsonIgnore: evita que Jackson entre en un ciclo infinito al serializar
    // (Reservation ya incluye su TravelPackage completo en el JSON).
    // @ToString.Exclude / @EqualsAndHashCode.Exclude: por la misma razón,
    // pero para los metodos que genera Lombok con @Data.
    @OneToMany(mappedBy = "travelPackage", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reservation> reservations;
}