package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public List<User> findAll() { return userRepository.findAll(); }

    @Override
    public User findById(Long id) { return userRepository.findById(id).orElse(null); }

    @Override
    public void save(User user) { userRepository.save(user); }

    @Override
    public void update(User user) { userRepository.save(user); }

    @Override
    public void delete(Long id) { userRepository.deleteById(id); }

    @Override
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
}