package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class IngredientDto {
    @NotBlank
    private String name;
    @Positive
    private double quantity;
    @NotBlank
    private String unit;
    @FutureOrPresent
    private LocalDate expiryDate;
    @Positive
    private double reorderThreshold;
}
