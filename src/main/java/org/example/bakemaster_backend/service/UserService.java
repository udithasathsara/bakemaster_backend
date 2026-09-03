package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.UserDto;
import org.example.bakemaster_backend.entity.UserEntity;
import org.example.bakemaster_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public UserEntity createUser(UserDto dto) {
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setActive(true);
        return userRepo.save(user);
    }

    public UserEntity updateUser(Long id, UserDto dto) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getRole() != null && !dto.getRole().isBlank()) user.setRole(dto.getRole());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return userRepo.save(user);
    }

    public UserEntity updateUserRole(Long id, String newRole) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return userRepo.save(user);
    }

    public void toggleUserStatus(Long id) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepo.save(user);
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
