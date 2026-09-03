package org.example.bakemaster_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Channel is required (WALK_IN, PHONE, EMAIL)")
    private String channel; // WALK_IN, PHONE, EMAIL

    private String deliveryAddress;

    @FutureOrPresent(message = "Delivery deadline cannot be in the past")
    private LocalDate deliveryDeadline;

    private String paymentStatus; // PENDING, PARTIAL_DEPOSIT, PAID
    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> items;
}
