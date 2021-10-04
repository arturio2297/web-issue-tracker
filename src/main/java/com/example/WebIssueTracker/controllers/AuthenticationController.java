package com.example.WebIssueTracker.controllers;


import com.example.WebIssueTracker.models.UserModel;
import com.example.WebIssueTracker.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user",new UserModel());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserModel user,
                               BindingResult bindingResult,
                               @RequestParam String password_again) {
        if (userService.userExist(user.getNickname())) {
            bindingResult.addError(new FieldError("user", "nickname",
                    "User with the same nickname already exist"));
        }
        if (!user.getPassword().equals(password_again)) {
            bindingResult.addError(new FieldError("user","password",
                    "Value of password fields must be the same"));
        }
        if (bindingResult.hasErrors()) return "registration";
        userService.registerUser(user);
        return "redirect:/login";
    }
}
