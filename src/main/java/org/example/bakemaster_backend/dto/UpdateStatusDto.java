package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusDto {
    @NotBlank
    private String status; // PENDING, IN_PROGRESS, BAKING, DELIVERED
}
