package com.travelgo.userservice.service;

import com.travelgo.userservice.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    boolean existsById(Long id);
    User create(User user);
    void delete(Long id);
}
