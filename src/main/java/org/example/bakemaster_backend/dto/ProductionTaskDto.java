package org.example.bakemaster_backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionTaskDto {
    private Long id;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    private Long orderItemId;
    private String productName;
    private int quantity;

    @NotBlank(message = "Task type is required")
    private String taskType; // BAKING, DECORATING, PACKAGING, DELIVERY

    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED

    private Long assignedStaffId;

    @NotNull(message = "Scheduled start is required")
    private LocalDateTime scheduledStart;

    @NotNull(message = "Scheduled end is required")
    private LocalDateTime scheduledEnd;

    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;

    private int priority = 2; // 1-High, 2-Medium, 3-Low
    private String notes;
}
