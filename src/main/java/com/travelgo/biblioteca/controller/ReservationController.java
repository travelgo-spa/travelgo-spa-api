package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    private final ReservationRepository repo;
    private final RestTemplate restTemplate;

    
    public ReservationController(ReservationRepository repo, RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<Reservation> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Reservation res) {
        String urlUsuarios = "http://localhost:8082/api/users/check/" + res.getUserId();
        
        try {
            
            Boolean userExists = restTemplate.getForObject(urlUsuarios, Boolean.class);
            
            if (userExists == null || !userExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error: El usuario con ID " + res.getUserId() + " no existe en el microservicio.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Error: El microservicio de usuarios (puerto 8082) no está respondiendo.");
        }

        
        Reservation saved = repo.save(res);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}