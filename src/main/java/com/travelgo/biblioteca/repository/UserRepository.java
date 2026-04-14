package com.travelgo.biblioteca.repository;

import com.travelgo.biblioteca.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Aquí ya tienes métodos como save(), findAll() y delete() gratis
}