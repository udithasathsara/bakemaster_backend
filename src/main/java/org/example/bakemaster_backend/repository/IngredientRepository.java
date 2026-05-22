package org.example.bakemaster_backend.repository;


import org.example.bakemaster_backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByQuantityLessThan(double threshold);
    List<Ingredient> findByExpiryDateBefore(LocalDate date);
}
