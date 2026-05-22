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
    private Long customerId;
    private LocalDate orderDate;
    private String status;          // PENDING, IN_PROGRESS, BAKING, DELIVERED
    private String channel;         // WALK_IN, PHONE, EMAIL
    private String deliveryAddress;
    private LocalDate deliveryDeadline;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
