package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeItemDto {
    private Long id;

    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    private String ingredientName;

    @Positive(message = "Quantity required must be positive")
    private Double quantityRequired = 0.0;

    private String unit;
    private Double estimatedCost = 0.0;
}
