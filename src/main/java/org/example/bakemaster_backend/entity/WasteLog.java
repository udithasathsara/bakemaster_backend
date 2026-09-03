package org.example.bakemaster_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Column(nullable = false)
    private String ingredientName;

    @Column(nullable = false)
    private double quantity;

    private String unit;

    @Column(nullable = false)
    private String reason; // EXPIRED, SPOILED, BURNT_IN_OVEN, DAMAGED_IN_TRANSIT, OTHER

    @Column(nullable = false)
    private LocalDate wasteDate;

    private double estimatedCostLoss;

    private String loggedBy;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (wasteDate == null) {
            wasteDate = LocalDate.now();
        }
    }
}
