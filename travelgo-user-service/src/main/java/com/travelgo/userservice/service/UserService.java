package com.travelgo.userservice.service;

import com.travelgo.userservice.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    boolean existsById(Integer id);
    User create(User user);
    void delete(Integer id);
}
