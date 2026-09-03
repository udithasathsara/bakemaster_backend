package org.example.bakemaster_backend.repository;

import org.example.bakemaster_backend.entity.ProductionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductionTaskRepository extends JpaRepository<ProductionTask, Long> {
    List<ProductionTask> findByOrderId(Long orderId);
    List<ProductionTask> findByStatus(String status);
    List<ProductionTask> findByAssignedStaffId(Long staffId);
    List<ProductionTask> findByScheduledStartBetween(LocalDateTime start, LocalDateTime end);
    long countByOrderIdAndStatusNot(Long orderId, String status);
    long countByStatus(String status);

    @org.springframework.data.jpa.repository.Query(
        "SELECT t FROM ProductionTask t WHERE t.assignedStaffId = :staffId " +
        "AND t.status NOT IN ('COMPLETED', 'CANCELLED') " +
        "AND (:taskId IS NULL OR t.id <> :taskId) " +
        "AND t.scheduledStart < :end AND t.scheduledEnd > :start"
    )
    List<ProductionTask> findConflictingTasks(
            @org.springframework.data.repository.query.Param("staffId") Long staffId,
            @org.springframework.data.repository.query.Param("start") LocalDateTime start,
            @org.springframework.data.repository.query.Param("end") LocalDateTime end,
            @org.springframework.data.repository.query.Param("taskId") Long taskId
    );
}
