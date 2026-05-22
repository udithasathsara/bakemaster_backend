package org.example.bakemaster_backend.repository;


import org.example.bakemaster_backend.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByStatusNot(String status);
    long countByStatus(String status);
    List<OrderEntity> findByCustomerId(Long customerId);
}