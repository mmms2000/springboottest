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

    // LIST PAGE: GET /admin/users
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "admin"; // templates/admin.html
    }

    // CREATE: POST /admin/users
    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user,
                         BindingResult result,
                         @RequestParam("rawPassword") String rawPassword,
                         @RequestParam(value = "roles", required = false) List<String> roles,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "admin";
        }

        userService.createUser(user, rawPassword, roles);
        return "redirect:/admin/users";
    }

    // EDIT FORM: GET /admin/users/{id}/edit
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "edit";
    }

    // UPDATE: POST /admin/users/{id}
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") User formUser,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            formUser.setId(id);
            return "edit";
        }

        User existing = userService.findById(id);
        existing.setFirstName(formUser.getFirstName());
        existing.setLastName(formUser.getLastName());
        existing.setEmail(formUser.getEmail());

        userService.update(existing);
        return "redirect:/admin/users";
    }

    // DELETE: POST /admin/users/{id}/delete
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}