package com.travelgo.userservice.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.travelgo.userservice.model.User;
import com.travelgo.userservice.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Endpoints básicos de usuarios")
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // Precarga automática de los 5 usuarios obligatorios
    @PostConstruct
    public void initMigration() {
        if (repo.count() == 0) {
            repo.save(new User(null, "italo_camp", "italo@travelgo.cl", "Ítalo Campodónico"));
            repo.save(new User(null, "robert_prof", "roberto@duoc.cl", "Roberto Profesor"));
            repo.save(new User(null, "felipedeg", "fe.degoyeneche@duocuc.cl", "Marcella Campodonico"));
            repo.save(new User(null, "user_test", "test@travelgo.cl", "Usuario de Pruebas"));
            repo.save(new User(null, "admin_travel", "admin@travelgo.cl", "Administrador Global"));
        }
    }

    @GetMapping
    public List<User> getAll() {
        return repo.findAll();
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable Long id) {
        return ResponseEntity.ok(repo.existsById(id));
    }
}