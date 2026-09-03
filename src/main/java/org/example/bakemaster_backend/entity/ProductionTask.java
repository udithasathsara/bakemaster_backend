package org.example.bakemaster_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long orderItemId;

    private String productName;
    private int quantity;

    @Column(name = "task_type")
    private String taskType; // "BAKING", "DECORATING", "PACKAGING", "DELIVERY"

    private String status; // "PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"

    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;

    @Column(name = "scheduled_start")
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end")
    private LocalDateTime scheduledEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    private int priority; // 1-High, 2-Medium, 3-Low

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
