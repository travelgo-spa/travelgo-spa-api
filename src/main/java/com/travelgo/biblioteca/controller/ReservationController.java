package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    private final ReservationRepository repo;

    public ReservationController(ReservationRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Reservation> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Reservation create(@RequestBody Reservation res) {
        return repo.save(res);
    }
}