package com.travelgo.userservice.controller;

import com.travelgo.userservice.model.User;
import com.travelgo.userservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Endpoints del microservicio de usuarios")

public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    
    @Operation(summary = "Listar usuarios")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        log.debug("GET /api/users - listando usuarios");
        return ResponseEntity.ok(service.findAll());
    }

    
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Integer id) {
        log.debug("GET /api/users/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    
    @Operation(summary = "Verificar existencia de usuario")
    @ApiResponse(responseCode = "200", description = "Resultado booleano de existencia")
    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkExists(@PathVariable Integer id) {
        log.debug("GET /api/users/check/{} - verificando existencia", id);
        boolean exists = service.existsById(id);
        log.info("Verificación de usuario id={}: existe={}", id, exists);
        return ResponseEntity.ok(exists);
    }

    
    @Operation(summary = "Crear usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "422", description = "Username o email duplicado")
    })
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        log.info("POST /api/users - creando usuario: '{}'", user.getUsername());
        User created = service.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /api/users/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
