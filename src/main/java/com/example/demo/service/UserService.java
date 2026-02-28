package com.example.demo.service;

import com.example.demo.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);

    void save(User user);
    void update(User user);
    void delete(Long id);

    void createUser(User user, String rawPassword, java.util.List<String> roles);
}