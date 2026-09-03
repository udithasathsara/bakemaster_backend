package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDto {
    private Long id;

    @NotBlank(message = "Ingredient name is required")
    private String name;

    @PositiveOrZero(message = "Quantity cannot be negative")
    private double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    private LocalDate expiryDate;

    @PositiveOrZero(message = "Reorder threshold cannot be negative")
    private double reorderThreshold;

    private double costPerUnit;
}
