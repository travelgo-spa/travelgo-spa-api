package com.travelgo.userservice.service.impl;

import com.travelgo.userservice.exception.BusinessException;
import com.travelgo.userservice.exception.NotFoundException;
import com.travelgo.userservice.model.User;
import com.travelgo.userservice.repository.UserRepository;
import com.travelgo.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        List<User> users = repo.findAll();
        log.info("Se encontraron {} usuarios registrados", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Integer id) {
        log.debug("Buscando usuario con id: {}", id);
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new NotFoundException("Usuario no encontrado con id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        boolean exists = repo.existsById(id);
        log.debug("¿Existe usuario con id {}? -> {}", id, exists);
        return exists;
    }

    @Override
    public User create(User user) {
        log.info("Creando usuario: '{}'", user.getUsername());

        // Regla de negocio: username único
        if (repo.existsByUsername(user.getUsername())) {
            throw new BusinessException("Ya existe un usuario con el username: " + user.getUsername());
        }

        // Regla de negocio: email único
        if (repo.existsByEmail(user.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + user.getEmail());
        }

        User saved = repo.save(user);
        log.info("Usuario creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Integer id) {
        log.info("Eliminando usuario con id: {}", id);
        findById(id); // Verifica que existe antes de borrar
        repo.deleteById(id);
        log.info("Usuario id: {} eliminado correctamente", id);
    }
}