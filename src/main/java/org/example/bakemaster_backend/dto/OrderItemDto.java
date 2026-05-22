package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemDto {
    @NotBlank
    private String productName;
    @Positive
    private int quantity;
    @Positive
    private double unitPrice;
}
