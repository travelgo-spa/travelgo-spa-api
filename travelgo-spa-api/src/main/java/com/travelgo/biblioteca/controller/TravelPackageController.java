package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.service.TravelPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    
    @Operation(summary = "Listar paquetes turísticos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<TravelPackage>> findAll() {
        log.debug("GET /api/packages - listando todos los paquetes");
        return ResponseEntity.ok(service.findAll());
    }

    
    @Operation(summary = "Buscar paquete turístico por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paquete encontrado"),
            @ApiResponse(responseCode = "404", description = "Paquete no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TravelPackage> findById(@PathVariable Long id) {
        log.debug("GET /api/packages/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    
    @Operation(summary = "Buscar paquetes turísticos por nombre")
    @ApiResponse(responseCode = "200", description = "Búsqueda ejecutada correctamente")
    @GetMapping("/search")
    public ResponseEntity<List<TravelPackage>> searchByName(@RequestParam String q) {
        log.debug("GET /api/packages/search?q={}", q);
        return ResponseEntity.ok(service.searchByName(q));
    }

    
    @Operation(summary = "Crear paquete turístico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paquete creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    @PostMapping
    public ResponseEntity<TravelPackage> create(@Valid @RequestBody TravelPackage travelPackage) {
        log.info("POST /api/packages - creando paquete: '{}'", travelPackage.getName());
        TravelPackage created = service.create(travelPackage);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    
    @Operation(summary = "Actualizar paquete turístico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paquete actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Paquete no encontrado"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TravelPackage> update(
            @PathVariable Long id,
            @Valid @RequestBody TravelPackage travelPackage) {
        log.info("PUT /api/packages/{}", id);
        return ResponseEntity.ok(service.update(id, travelPackage));
    }

    
    @Operation(summary = "Eliminar paquete turístico")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paquete eliminado"),
            @ApiResponse(responseCode = "404", description = "Paquete no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/packages/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
