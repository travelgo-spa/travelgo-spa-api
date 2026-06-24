package com.travelgo.biblioteca.controller;

import com.travelgo.biblioteca.model.User;
import com.travelgo.biblioteca.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        log.debug("GET /api/users - listando usuarios");
        return ResponseEntity.ok(service.findAll());
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        log.debug("GET /api/users/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        log.info("POST /api/users - creando usuario: '{}'", user.getUsername());
        User created = service.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
