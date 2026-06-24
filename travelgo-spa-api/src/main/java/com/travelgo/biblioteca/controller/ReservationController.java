package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
@Tag(name = "Reservas", description = "Gestión de reservas de paquetes turísticos")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    
    @GetMapping
    public ResponseEntity<List<Reservation>> getAll() {
        log.debug("GET /api/reservations - listando reservas");
        return ResponseEntity.ok(service.findAll());
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getByUser(@PathVariable Long userId) {
        log.debug("GET /api/reservations/user/{}", userId);
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    
    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody Reservation reservation) {
        log.info("POST /api/reservations - userId={}, packageId={}",
                reservation.getUserId(), reservation.getPackageId());
        Reservation created = service.create(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/reservations/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
