package com.example.demo.service;

import com.example.demo.model.User;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);

    void save(User user);
    void update(User user);
    void delete(Long id);

    void createUser(User user, String rawPassword, java.util.List<String> roles);

    boolean emailExists(String email);

    boolean emailExistsForOtherUser(String email, Long id);

    void updateUserAdmin(Long id, @Valid User formUser, List<String> roles, String newPassword);

    @Nullable Object getRoleNames(User user);

    User findByEmail(String email);
}