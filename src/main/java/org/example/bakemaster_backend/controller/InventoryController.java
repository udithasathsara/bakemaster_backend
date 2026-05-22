package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.IngredientDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.PurchaseOrder;
import org.example.bakemaster_backend.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService) { this.inventoryService = inventoryService; }

    @GetMapping
    public List<Ingredient> getAll() { return inventoryService.getAll(); }

    @PostMapping
    public Ingredient add(@Valid @RequestBody IngredientDto dto) {
        return inventoryService.add(dto);
    }

    @PutMapping("/{id}/stock")
    public Ingredient updateStock(@PathVariable Long id, @RequestParam double qty) {
        return inventoryService.updateStock(id, qty);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { inventoryService.delete(id); }

    @PostMapping("/generate-po")
    public PurchaseOrder generatePO() { return inventoryService.generatePOFromLowStock(); }
}
