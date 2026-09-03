package org.example.bakemaster_backend;

import org.example.bakemaster_backend.dto.ProductDto;
import org.example.bakemaster_backend.dto.RecipeItemDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.Product;
import org.example.bakemaster_backend.entity.RecipeItem;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.ProductRepository;
import org.example.bakemaster_backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepo;

    @Mock
    private IngredientRepository ingredientRepo;

    @InjectMocks
    private ProductService productService;

    private Ingredient flour;
    private Ingredient butter;

    @BeforeEach
    void setUp() {
        flour = new Ingredient();
        flour.setId(1L);
        flour.setName("Flour");
        flour.setQuantity(10.0); // 10 kg
        flour.setUnit("kg");
        flour.setCostPerUnit(2.0);

        butter = new Ingredient();
        butter.setId(2L);
        butter.setName("Butter");
        butter.setQuantity(5.0); // 5 kg
        butter.setUnit("kg");
        butter.setCostPerUnit(8.0);
    }

    @Test
    void testCreateProductCalculatesCostCorrectly() {
        when(ingredientRepo.findById(1L)).thenReturn(Optional.of(flour));
        when(ingredientRepo.findById(2L)).thenReturn(Optional.of(butter));
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDto dto = new ProductDto();
        dto.setName("Butter Loaf");
        dto.setCategory("BREAD");
        dto.setSellingPrice(15.0);

        RecipeItemDto ri1 = new RecipeItemDto(null, 1L, "Flour", 0.5, "kg", 0.0);
        RecipeItemDto ri2 = new RecipeItemDto(null, 2L, "Butter", 0.2, "kg", 0.0);
        dto.setRecipeItems(List.of(ri1, ri2));

        ProductDto created = productService.createProduct(dto);

        assertNotNull(created);
        assertEquals("Butter Loaf", created.getName());
        // Cost should be: (0.5 * 2.0) + (0.2 * 8.0) = 1.0 + 1.6 = 2.6
        assertEquals(2.6, created.getCostPrice(), 0.001);
    }

    @Test
    void testCalculateMaxProducibleBasedOnIngredients() {
        Product cake = new Product();
        cake.setId(10L);
        cake.setName("Chocolate Cake");

        RecipeItem item1 = new RecipeItem(1L, cake, flour, 0.5, "kg"); // 10.0 / 0.5 = 20 cakes
        RecipeItem item2 = new RecipeItem(2L, cake, butter, 0.5, "kg"); // 5.0 / 0.5 = 10 cakes
        cake.setRecipeItems(List.of(item1, item2));

        when(productRepo.findById(10L)).thenReturn(Optional.of(cake));

        int maxUnits = productService.calculateMaxProducible(10L);
        // Butter is the bottleneck (5kg / 0.5kg = 10 units)
        assertEquals(10, maxUnits);
    }
}
