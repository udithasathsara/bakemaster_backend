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
        user.setRole("ROLE_STAFF"); // default role
        userRepo.save(user);
        return "User registered successfully";
    }

    public String login(LoginDto dto) {
        UserEntity user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return tokenProvider.generateToken(user.getUsername(), user.getRole());
    }
}
