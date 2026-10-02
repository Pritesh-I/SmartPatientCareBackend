package com.smartcare.controller;

import com.smartcare.model.UserAccount;
import com.smartcare.repository.UserAccountRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountRepository repository;

    public AuthController(UserAccountRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserAccount user) {

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return Map.of("success", false, "message", "Username is required.");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return Map.of("success", false, "message", "Password is required.");
        }

        if (repository.findByUsername(user.getUsername()).isPresent()) {
            return Map.of("success", false,
                    "message", "Username already exists.");
        }

        UserAccount saved = repository.save(user);

        return Map.of(
                "success", true,
                "message", "Account created.",
                "id", saved.getId(),
                "username", saved.getUsername(),
                "role", saved.getRole()
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserAccount login) {

        UserAccount user =
                repository.findByUsername(login.getUsername()).orElse(null);

        if (user == null ||
                !user.getPassword().equals(login.getPassword())) {

            return Map.of(
                    "success", false,
                    "message", "Invalid username or password."
            );
        }

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Login successful.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return response;
    }
}
