package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // ===============================
    // LIST PAGE
    // ===============================

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "admin";
    }

    // ===============================
    // CREATE USER
    // ===============================

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user,
                         BindingResult result,
                         @RequestParam("rawPassword") String rawPassword,
                         @RequestParam(value = "roleName", required = false) List<String> roleName,
                         Model model) {

        // duplicate email check
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "duplicate", "Email already exists.");
        }

        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "admin";
        }

        userService.createUser(user, rawPassword, roleName);

        return "redirect:/admin/users";
    }

    // ===============================
    // UPDATE USER
    // ===============================

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") User formUser,
                         BindingResult result,
                         @RequestParam(value = "roleName", required = false) List<String> roleName,
                         @RequestParam(value = "newPassword", required = false) String newPassword,
                         Model model) {

        // duplicate email check (exclude current user)
        if (userService.emailExistsForOtherUser(formUser.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "Email already exists.");
        }

        if (result.hasErrors()) {
            // IMPORTANT: admin page needs these to render
            model.addAttribute("users", userService.findAll());
            model.addAttribute("user", new User()); // for Add User tab
            model.addAttribute("editUserId", id);   // to re-open modal
            return "admin"; //
        }

        userService.updateUserAdmin(id, formUser, roleName, newPassword);
        return "redirect:/admin/users";
    }
    // ===============================
    // DELETE USER
    // ===============================

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {

        userService.delete(id);

        return "redirect:/admin/users";
    }
}