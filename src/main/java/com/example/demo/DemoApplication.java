package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // 👇 PASTE IT RIGHT HERE (inside class, below main)

    @Bean
    public org.springframework.boot.CommandLineRunner init(
            com.example.demo.repository.UserRepository userRepository,
            com.example.demo.repository.RoleRepository roleRepository,
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder) {

        return args -> {

            com.example.demo.model.Role adminRole =
                    roleRepository.findByName("ROLE_ADMIN");

            if (adminRole == null) {
                adminRole = roleRepository.save(
                        new com.example.demo.model.Role("ROLE_ADMIN"));
            }

            if (userRepository.findByEmail("admin@mail.com") == null) {

                com.example.demo.model.User admin =
                        new com.example.demo.model.User();

                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@mail.com");
                admin.setPassword(encoder.encode("admin"));
                admin.setRoles(java.util.Set.of(adminRole));

                userRepository.save(admin);
            }
        };
    }
}