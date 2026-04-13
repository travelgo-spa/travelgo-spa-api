package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.service.TravelPackageService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen
public class TravelPackageController {

    private final TravelPackageService service;

    public TravelPackageController(TravelPackageService service) {
        this.service = service;
    }

    // 1. GET (All): Listar todos los registros
    @GetMapping
    public List<TravelPackage> findAll() {
        return service.findAll();
    }

    // 2. GET (by ID): Buscar un registro específico
    @GetMapping("/{id}")
    public ResponseEntity<TravelPackage> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

   
    @GetMapping("/search")
    public List<TravelPackage> searchByName(@RequestParam String q) {
        return service.searchByName(q);
    }

    // 3. POST: Crear nuevos registros
    @PostMapping
    public ResponseEntity<TravelPackage> create(@Valid @RequestBody TravelPackage travelPackage) {
        TravelPackage created = service.create(travelPackage);
        return new ResponseEntity<>(created, HttpStatus.CREATED); // 201 Created
    }

  
    @PutMapping("/{id}")
    public ResponseEntity<TravelPackage> update(@PathVariable Long id, @Valid @RequestBody TravelPackage travelPackage) {
        return ResponseEntity.ok(service.update(id, travelPackage));
    }

 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}