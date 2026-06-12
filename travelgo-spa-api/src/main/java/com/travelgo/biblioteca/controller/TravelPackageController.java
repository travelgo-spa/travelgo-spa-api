package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.service.TravelPackageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*")
@Tag(name = "Paquetes turísticos", description = "Gestión de paquetes de viaje disponibles")

public class TravelPackageController {

    private static final Logger log = LoggerFactory.getLogger(TravelPackageController.class);

    private final TravelPackageService service;

    public TravelPackageController(TravelPackageService service) {
        this.service = service;
    }

    
    @GetMapping
    public ResponseEntity<List<TravelPackage>> findAll() {
        log.debug("GET /api/packages - listando todos los paquetes");
        return ResponseEntity.ok(service.findAll());
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<TravelPackage> findById(@PathVariable Long id) {
        log.debug("GET /api/packages/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    
    @GetMapping("/search")
    public ResponseEntity<List<TravelPackage>> searchByName(@RequestParam String q) {
        log.debug("GET /api/packages/search?q={}", q);
        return ResponseEntity.ok(service.searchByName(q));
    }

    
    @PostMapping
    public ResponseEntity<TravelPackage> create(@Valid @RequestBody TravelPackage travelPackage) {
        log.info("POST /api/packages - creando paquete: '{}'", travelPackage.getName());
        TravelPackage created = service.create(travelPackage);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<TravelPackage> update(
            @PathVariable Long id,
            @Valid @RequestBody TravelPackage travelPackage) {
        log.info("PUT /api/packages/{}", id);
        return ResponseEntity.ok(service.update(id, travelPackage));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/packages/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
