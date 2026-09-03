package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreateDto {
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private LocalDate orderDate;

    @NotEmpty(message = "Purchase order must contain at least one item")
    private List<PurchaseOrderItemInput> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderItemInput {
        private Long ingredientId;
        private String ingredientName;
        private double quantity;
        private double unitPrice;
    }
}