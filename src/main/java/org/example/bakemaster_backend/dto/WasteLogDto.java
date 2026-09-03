package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteLogDto {
    private Long id;

    private Long ingredientId;

    @NotBlank(message = "Ingredient name is required")
    private String ingredientName;

    @Positive(message = "Quantity must be greater than 0")
    private double quantity;

    private String unit;

    @NotBlank(message = "Reason is required (EXPIRED, SPOILED, BURNT_IN_OVEN, DAMAGED_IN_TRANSIT, OTHER)")
    private String reason;

    private LocalDate wasteDate;
    private double estimatedCostLoss;
    private String loggedBy;
    private String notes;
}
