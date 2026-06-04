package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.User;
import com.travelgo.biblioteca.repository.UserRepository;
import com.travelgo.biblioteca.service.UserService;
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
        log.info("Se encontraron {} usuarios", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        log.debug("Buscando usuario con id: {}", id);
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new NotFoundException("Usuario no encontrado con id: " + id);
                });
    }

    @Override
    public User create(User user) {
        log.info("Creando usuario: '{}'", user.getUsername());

        // Regla de negocio: el email no puede estar duplicado
        if (repo.existsByEmail(user.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + user.getEmail());
        }

        User saved = repo.save(user);
        log.info("Usuario creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando usuario con id: {}", id);
        findById(id); // Verifica que existe
        repo.deleteById(id);
        log.info("Usuario id: {} eliminado correctamente", id);
    }
}
