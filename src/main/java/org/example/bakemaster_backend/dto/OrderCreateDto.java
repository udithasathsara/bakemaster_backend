package org.example.bakemaster_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderCreateDto {
    @NotNull
    private Long customerId;
    @NotBlank
    private String channel; // WALK_IN, PHONE, EMAIL
    private String deliveryAddress;
    @FutureOrPresent
    private LocalDate deliveryDeadline;
    @NotEmpty
    @Valid
    private List<OrderItemDto> items;
}

