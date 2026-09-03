package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

@Data
public class StaffDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private String role;

    private String phone;
    @Email(message = "Invalid email")
    private String email;

    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private boolean active = true;
}
