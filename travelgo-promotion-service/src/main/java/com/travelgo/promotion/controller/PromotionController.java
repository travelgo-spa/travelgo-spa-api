package com.travelgo.promotion.controller;

import com.travelgo.promotion.model.Promotion;
import com.travelgo.promotion.service.PromotionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@Tag(name = "Promociones", description = "Gestion de codigos de descuento")
public class PromotionController {

    private static final Logger log = LoggerFactory.getLogger(PromotionController.class);

    private final PromotionService service;

    public PromotionController(PromotionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> findAll() {
        log.debug("GET /api/promotions - listando promociones");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Promotion> validateCode(@PathVariable String code) {
        log.debug("GET /api/promotions/validate/{}", code);
        return ResponseEntity.ok(service.validateCode(code));
    }

    @PostMapping
    public ResponseEntity<Promotion> create(@Valid @RequestBody Promotion promotion) {
        log.info("POST /api/promotions - codigo: '{}'", promotion.getCode());
        Promotion created = service.create(promotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/promotions/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}