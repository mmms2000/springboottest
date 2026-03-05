package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // =========================
    // CREATE USER
    // =========================

    @Override
    @Transactional
    public void createUser(User user, String rawPassword, List<String> roles) {

        if (roles == null || roles.isEmpty()) {
            roles = List.of("ROLE_USER");
        }

        Set<Role> roleEntities = new HashSet<>();

        for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName);

            if (role == null) {
                role = roleRepository.save(new Role(roleName));
            }

            roleEntities.add(role);
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(roleEntities);

        userRepository.save(user);
    }

    // =========================
    // DUPLICATE EMAIL CHECK
    // =========================

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean emailExistsForOtherUser(String email, Long userId) {
        return userRepository.existsByEmailAndIdNot(email, userId);
    }

    // =========================
    // ROLE NAMES FOR EDIT PAGE
    // =========================

    @Override
    public List<String> getRoleNames(User user) {
        if (user == null || user.getRoles() == null) {
            return List.of();
        }

        return user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    // =========================
    // ADMIN UPDATE USER
    // =========================

    @Override
    @Transactional
    public void updateUserAdmin(Long id, User formUser, List<String> roles, String newPassword) {

        User existing = userRepository.findById(id).orElseThrow();

        existing.setFirstName(formUser.getFirstName());
        existing.setLastName(formUser.getLastName());
        existing.setEmail(formUser.getEmail());

        // roles
        if (roles == null || roles.isEmpty()) {
            roles = List.of("ROLE_USER");
        }

        Set<Role> roleEntities = new HashSet<>();

        for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName);

            if (role == null) {
                role = roleRepository.save(new Role(roleName));
            }

            roleEntities.add(role);
        }

        existing.setRoles(roleEntities);

        // optional password change
        if (newPassword != null && !newPassword.isBlank()) {
            existing.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(existing);
    }
}