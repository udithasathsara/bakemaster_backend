package org.example.bakemaster_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bakery_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    private LocalDate orderDate;

    // Standard lifecycle: RECEIVED -> CONFIRMED -> IN_PROGRESS -> COMPLETED -> DELIVERED (or CANCELLED)
    @Column(nullable = false)
    private String status;

    private String channel;         // WALK_IN, PHONE, EMAIL
    private String deliveryAddress;
    private LocalDate deliveryDeadline;

    private double totalAmount;

    private String paymentStatus;   // PENDING, PARTIAL_DEPOSIT, PAID

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
