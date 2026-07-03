package com.travelgo.biblioteca.service;

import com.travelgo.biblioteca.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User create(User user);
    void delete(Long id);
}
