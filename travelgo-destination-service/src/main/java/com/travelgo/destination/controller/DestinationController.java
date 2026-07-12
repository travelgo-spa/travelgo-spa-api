package com.travelgo.destination.controller;

import com.travelgo.destination.model.Destination;
import com.travelgo.destination.service.DestinationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@Tag(name = "Destinos", description = "Catalogo de destinos turisticos")
public class DestinationController {

    private static final Logger log = LoggerFactory.getLogger(DestinationController.class);

    private final DestinationService service;

    public DestinationController(DestinationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Destination>> findAll() {
        log.debug("GET /api/destinations - listando destinos");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Destination> findById(@PathVariable Long id) {
        log.debug("GET /api/destinations/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Destination> create(@Valid @RequestBody Destination destination) {
        log.info("POST /api/destinations - creando: '{}'", destination.getName());
        Destination created = service.create(destination);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/destinations/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}