package com.example.demo.controller;

import com.example.demo.model.User;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "users";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "users";
        }

        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "edit";
    }

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

        return "redirect:/users";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/users";
    }
}
