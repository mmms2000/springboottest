package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return userService.findAll().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toMap(user));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        User user = fromMap(body);
        List<String> roles = (List<String>) body.get("roles");
        String password = (String) body.get("password");

        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        userService.createUser(user, password, roles);
        return ResponseEntity.ok(Map.of("message", "User created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        User formUser = fromMap(body);
        List<String> roles = (List<String>) body.get("roles");
        String newPassword = (String) body.get("password");

        if (userService.emailExistsForOtherUser(formUser.getEmail(), id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        userService.updateUserAdmin(id, formUser, roles, newPassword);

        boolean selfEdit = currentUser.getUsername().equals(formUser.getEmail());
        boolean stillAdmin = roles != null && roles.contains("ROLE_ADMIN");
        if (selfEdit && !stillAdmin) {
            return ResponseEntity.ok(Map.of("message", "User updated", "forceLogout", true));
        }

        return ResponseEntity.ok(Map.of("message", "User updated", "forceLogout", false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    // --- helpers ---

    private Map<String, Object> toMap(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        return Map.of(
                "id",        user.getId(),
                "firstName", user.getFirstName(),
                "lastName",  user.getLastName(),
                "age",       user.getAge(),
                "email",     user.getEmail(),
                "roles",     roleNames
        );
    }

    private User fromMap(Map<String, Object> body) {
        User user = new User();
        user.setFirstName((String) body.get("firstName"));
        user.setLastName((String) body.get("lastName"));
        user.setEmail((String) body.get("email"));
        user.setAge(body.get("age") != null ? Integer.parseInt(body.get("age").toString()) : 0);
        return user;
    }
}