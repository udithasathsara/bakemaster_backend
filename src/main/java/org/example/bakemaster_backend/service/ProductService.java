package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.ProductDto;
import org.example.bakemaster_backend.dto.RecipeItemDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.Product;
import org.example.bakemaster_backend.entity.RecipeItem;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final IngredientRepository ingredientRepo;

    public ProductService(ProductRepository productRepo, IngredientRepository ingredientRepo) {
        this.productRepo = productRepo;
        this.ingredientRepo = ingredientRepo;
    }

    public List<ProductDto> getAllActiveProducts() {
        return productRepo.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getByCategory(String category) {
        return productRepo.findByCategoryAndActiveTrue(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToDto(product);
    }

    public Product getProductEntity(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setSellingPrice(dto.getSellingPrice());
        product.setShelfLifeDays(dto.getShelfLifeDays());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setActive(true);

        double totalCost = 0.0;
        List<RecipeItem> items = new ArrayList<>();

        if (dto.getRecipeItems() != null) {
            for (RecipeItemDto itemDto : dto.getRecipeItems()) {
                Ingredient ingredient = ingredientRepo.findById(itemDto.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + itemDto.getIngredientId()));

                RecipeItem recipeItem = new RecipeItem();
                recipeItem.setProduct(product);
                recipeItem.setIngredient(ingredient);
                recipeItem.setQuantityRequired(itemDto.getQuantityRequired());
                recipeItem.setUnit(itemDto.getUnit() != null ? itemDto.getUnit() : ingredient.getUnit());

                totalCost += (ingredient.getCostPerUnit() * itemDto.getQuantityRequired());
                items.add(recipeItem);
            }
        }

        product.setCostPrice(totalCost);
        product.setRecipeItems(items);
        Product saved = productRepo.save(product);
        return mapToDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setSellingPrice(dto.getSellingPrice());
        product.setShelfLifeDays(dto.getShelfLifeDays());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setActive(dto.isActive());

        product.getRecipeItems().clear();

        double totalCost = 0.0;
        if (dto.getRecipeItems() != null) {
            for (RecipeItemDto itemDto : dto.getRecipeItems()) {
                Ingredient ingredient = ingredientRepo.findById(itemDto.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + itemDto.getIngredientId()));

                RecipeItem recipeItem = new RecipeItem();
                recipeItem.setProduct(product);
                recipeItem.setIngredient(ingredient);
                recipeItem.setQuantityRequired(itemDto.getQuantityRequired());
                recipeItem.setUnit(itemDto.getUnit() != null ? itemDto.getUnit() : ingredient.getUnit());

                totalCost += (ingredient.getCostPerUnit() * itemDto.getQuantityRequired());
                product.getRecipeItems().add(recipeItem);
            }
        }

        product.setCostPrice(totalCost);
        Product updated = productRepo.save(product);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setActive(false); // soft delete
        productRepo.save(product);
    }

    /**
     * Calculates how many units of this product the bakery can produce right now
     * based on available raw ingredient quantities in stock.
     */
    public int calculateMaxProducible(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (product.getRecipeItems() == null || product.getRecipeItems().isEmpty()) {
            return 0;
        }

        int maxUnits = Integer.MAX_VALUE;
        for (RecipeItem item : product.getRecipeItems()) {
            Ingredient ing = item.getIngredient();
            if (item.getQuantityRequired() <= 0) continue;
            int possibleWithThisIngredient = (int) (ing.getQuantity() / item.getQuantityRequired());
            if (possibleWithThisIngredient < maxUnits) {
                maxUnits = possibleWithThisIngredient;
            }
        }
        return maxUnits == Integer.MAX_VALUE ? 0 : maxUnits;
    }

    public ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setCostPrice(product.getCostPrice());
        dto.setShelfLifeDays(product.getShelfLifeDays());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setActive(product.isActive());

        if (product.getRecipeItems() != null) {
            List<RecipeItemDto> itemDtos = product.getRecipeItems().stream().map(ri -> {
                RecipeItemDto idto = new RecipeItemDto();
                idto.setId(ri.getId());
                idto.setIngredientId(ri.getIngredient().getId());
                idto.setIngredientName(ri.getIngredient().getName());
                idto.setQuantityRequired(ri.getQuantityRequired());
                idto.setUnit(ri.getUnit());
                idto.setEstimatedCost(ri.getQuantityRequired() * ri.getIngredient().getCostPerUnit());
                return idto;
            }).collect(Collectors.toList());
            dto.setRecipeItems(itemDtos);
        }
        return dto;
    }
}
