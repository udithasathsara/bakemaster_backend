package org.example.bakemaster_backend.repository;

import org.example.bakemaster_backend.entity.WasteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {
    List<WasteLog> findByWasteDateBetween(LocalDate start, LocalDate end);
    List<WasteLog> findByReason(String reason);

    @Query("SELECT COALESCE(SUM(w.estimatedCostLoss), 0.0) FROM WasteLog w WHERE w.wasteDate BETWEEN :start AND :end")
    double sumLossBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT w.reason, COUNT(w), SUM(w.estimatedCostLoss) FROM WasteLog w GROUP BY w.reason")
    List<Object[]> getWasteSummaryByReason();
}
