package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role; // ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF

    private String fullName;
    @Email(message = "Invalid email format")
    private String email;
    private String phone;
}
