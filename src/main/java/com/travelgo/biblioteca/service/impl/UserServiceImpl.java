package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.model.User;
import com.travelgo.biblioteca.repository.UserRepository;
import com.travelgo.biblioteca.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public User create(User user) {
        return repo.save(user);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}