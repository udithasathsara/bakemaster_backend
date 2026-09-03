package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.LoginDto;
import org.example.bakemaster_backend.dto.RegisterDto;
import org.example.bakemaster_backend.entity.UserEntity;
import org.example.bakemaster_backend.repository.UserRepository;
import org.example.bakemaster_backend.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public String register(RegisterDto dto) {
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("ROLE_STAFF");
        user.setActive(true);
        userRepo.save(user);
        return "User registered successfully";
    }

    public String login(LoginDto dto) {
        System.out.println("=== Login Attempt ===");
        System.out.println("Username: " + dto.getUsername());
        System.out.println("Password: " + dto.getPassword());

        UserEntity user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    System.out.println("User not found!");
                    return new BadCredentialsException("Invalid username or password");
                });

        System.out.println("User found: " + user.getUsername());
        System.out.println("Role: " + user.getRole());
        System.out.println("Active: " + user.isActive());
        System.out.println("Stored password: " + user.getPassword());

        if (!user.isActive()) {
            System.out.println("Account deactivated!");
            throw new BadCredentialsException("Account is deactivated");
        }

        boolean passwordMatches = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        System.out.println("Password matches: " + passwordMatches);

        if (!passwordMatches) {
            System.out.println("Password incorrect!");
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole());
        System.out.println("Token generated: " + token.substring(0, Math.min(20, token.length())) + "...");
        System.out.println("====================");

        return token;
    }
}