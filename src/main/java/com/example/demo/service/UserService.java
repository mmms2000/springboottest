package com.example.demo.service;

import com.example.demo.model.User;

import java.util.List;


public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User save(User user);

    User update(User user);

    void delete(Long id);
}
