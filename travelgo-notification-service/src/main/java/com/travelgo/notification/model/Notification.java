package com.travelgo.notification.model;

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
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

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

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    @NotBlank(message = "La fecha de envio es obligatoria")
    @Column(name = "sent_at")
    private String sentAt;

    @Column(name = "read_status", columnDefinition = "INTEGER")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Boolean readStatus = false;
}