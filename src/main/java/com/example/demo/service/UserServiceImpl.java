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

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // =========================
    // ADMIN UPDATE USER
    // =========================

    @Override
    @Transactional
    public void updateUserAdmin(Long id, User formUser, List<String> roleName, String newPassword) {

        User user = userRepository.findById(id).orElseThrow();

        // update basic fields
        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setAge(formUser.getAge());
        user.setEmail(formUser.getEmail());

        // update password if provided
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // update roles
        Set<Role> roles = new HashSet<>();

        if (roleName != null) {
            for (String role : roleName) {
                Role r = roleRepository.findByName(role);

                if (r == null) {
                    throw new RuntimeException("Role not found");
                }
                roles.add(r);
            }
        }

        user.setRoles(roles);

        userRepository.save(user);
    }
}