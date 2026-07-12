package com.travelgo.notification.controller;

import com.travelgo.notification.model.Notification;
import com.travelgo.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones", description = "Gestion de notificaciones de usuario")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> findByUserId(@PathVariable Long userId) {
        log.debug("GET /api/notifications/user/{}", userId);
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Notification> create(@Valid @RequestBody Notification notification) {
        log.info("POST /api/notifications - userId={}", notification.getUserId());
        Notification created = service.create(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        log.info("PATCH /api/notifications/{}/read", id);
        return ResponseEntity.ok(service.markAsRead(id));
    }
}