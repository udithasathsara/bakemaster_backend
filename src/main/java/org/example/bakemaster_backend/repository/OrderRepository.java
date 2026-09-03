package org.example.bakemaster_backend.repository;

import org.example.bakemaster_backend.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByStatusNot(String status);
    List<OrderEntity> findByStatusNotIn(List<String> statuses);
    long countByStatus(String status);
    List<OrderEntity> findByCustomerId(Long customerId);

    List<OrderEntity> findByOrderDate(LocalDate orderDate);
    List<OrderEntity> findByOrderDateBetween(LocalDate start, LocalDate end);
    long countByOrderDate(LocalDate orderDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM OrderEntity o WHERE o.orderDate = :date AND o.status <> 'CANCELLED'")
    double sumSalesByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM OrderEntity o WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    double sumSalesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}