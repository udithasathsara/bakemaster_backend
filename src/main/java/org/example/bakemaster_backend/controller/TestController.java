package org.example.bakemaster_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint works!";
    }

    @PostMapping("/login-test")
    public String loginTest() {
        return "Login test works!";
    }
}
