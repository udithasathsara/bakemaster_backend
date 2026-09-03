package org.example.bakemaster_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category; // CAKE, BREAD, PASTRY, COOKIE, CUPCAKE

    @Positive(message = "Selling price must be positive")
    private double sellingPrice;

    private double costPrice;
    private int shelfLifeDays;
    private String description;
    private String imageUrl;
    private boolean active = true;

    @Valid
    private List<RecipeItemDto> recipeItems = new ArrayList<>();
}
