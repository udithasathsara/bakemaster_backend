package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    @Positive(message = "Unit price must be greater than 0")
    private double unitPrice;

    private String specialInstructions;
}
