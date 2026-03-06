package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {
        String email = principal.getName();
        User freshUser = userService.findByEmail(email); // reload from DB

        model.addAttribute("user", freshUser);
        return "user"; // templates/user.html
    }
}