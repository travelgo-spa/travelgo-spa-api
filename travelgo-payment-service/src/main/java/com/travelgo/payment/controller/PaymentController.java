package com.travelgo.payment.controller;

import com.travelgo.payment.model.Payment;
import com.travelgo.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Pagos", description = "Gestion de pagos de reservas")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> findAll() {
        log.debug("GET /api/payments - listando pagos");
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Payment> create(@Valid @RequestBody Payment payment) {
        log.info("POST /api/payments - reservationId={}", payment.getReservationId());
        Payment created = service.create(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/payments/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}