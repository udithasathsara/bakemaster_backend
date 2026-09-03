package org.example.bakemaster_backend.repository;

import org.example.bakemaster_backend.entity.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    List<RecipeItem> findByProductId(Long productId);
    List<RecipeItem> findByIngredientId(Long ingredientId);
}
