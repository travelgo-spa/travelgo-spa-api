package com.travelgo.wishlist.controller;

import com.travelgo.wishlist.model.WishlistItem;
import com.travelgo.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Lista de deseos de paquetes turisticos")
public class WishlistController {

    private static final Logger log = LoggerFactory.getLogger(WishlistController.class);

    private final WishlistService service;

    public WishlistController(WishlistService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishlistItem>> findByUserId(@PathVariable Long userId) {
        log.debug("GET /api/wishlist/user/{}", userId);
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<WishlistItem> create(@Valid @RequestBody WishlistItem item) {
        log.info("POST /api/wishlist - userId={}, packageId={}", item.getUserId(), item.getPackageId());
        WishlistItem created = service.create(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/wishlist/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}