package com.travelgo.support.controller;

import com.travelgo.support.model.SupportTicket;
import com.travelgo.support.service.SupportTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support-tickets")
@Tag(name = "Soporte", description = "Gestion de tickets de soporte al cliente")
public class SupportTicketController {

    private static final Logger log = LoggerFactory.getLogger(SupportTicketController.class);

    private final SupportTicketService service;

    public SupportTicketController(SupportTicketService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SupportTicket>> findByUserId(@PathVariable Long userId) {
        log.debug("GET /api/support-tickets/user/{}", userId);
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<SupportTicket> create(@Valid @RequestBody SupportTicket ticket) {
        log.info("POST /api/support-tickets - userId={}", ticket.getUserId());
        SupportTicket created = service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SupportTicket> updateStatus(@PathVariable Long id,
                                                        @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        log.info("PATCH /api/support-tickets/{}/status - nuevo estado: {}", id, newStatus);
        return ResponseEntity.ok(service.updateStatus(id, newStatus));
    }
}