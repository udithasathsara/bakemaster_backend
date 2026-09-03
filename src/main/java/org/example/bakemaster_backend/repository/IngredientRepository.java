package org.example.bakemaster_backend.repository;

import org.example.bakemaster_backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByQuantityLessThan(double threshold);
    List<Ingredient> findByExpiryDateBefore(LocalDate date);
    java.util.Optional<Ingredient> findByNameIgnoreCase(String name);

    @Query("SELECT i FROM Ingredient i WHERE i.quantity <= i.reorderThreshold")
    List<Ingredient> findLowStockIngredients();

    @Query(value = "SELECT * FROM ingredient WHERE expiry_date <= CURDATE()", nativeQuery = true)
    List<Ingredient> findExpiredIngredients();

    @Query(value = "SELECT * FROM ingredient WHERE expiry_date > CURDATE() AND DATEDIFF(expiry_date, CURDATE()) <= :days", nativeQuery = true)
    List<Ingredient> findExpiringWithinDays(@Param("days") int days);
}
