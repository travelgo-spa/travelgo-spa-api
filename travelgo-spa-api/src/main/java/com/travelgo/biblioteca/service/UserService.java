package com.travelgo.biblioteca.service;

import com.travelgo.biblioteca.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    User create(User user);
    void delete(Integer id);
}
