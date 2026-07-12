package com.travelgo.review.controller;

import com.travelgo.review.model.Review;
import com.travelgo.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Resenas", description = "Gestion de resenas de paquetes turisticos")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Review>> findAll() {
        log.debug("GET /api/reviews - listando resenas");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<Review>> findByPackageId(@PathVariable Long packageId) {
        log.debug("GET /api/reviews/package/{}", packageId);
        return ResponseEntity.ok(service.findByPackageId(packageId));
    }

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {
        log.info("POST /api/reviews - packageId={}", review.getPackageId());
        Review created = service.create(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/reviews/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
