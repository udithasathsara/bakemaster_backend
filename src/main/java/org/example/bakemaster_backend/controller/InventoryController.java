package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.IngredientDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.PurchaseOrder;
import org.example.bakemaster_backend.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Ingredient> getAll() {
        return inventoryService.getAll();
    }

    @GetMapping("/{id}")
    public Ingredient getById(@PathVariable Long id) {
        return inventoryService.getById(id);
    }

    @GetMapping("/alerts/expiry")
    public ResponseEntity<Map<String, Object>> getExpiryAlerts() {
        return ResponseEntity.ok(inventoryService.getExpiryAlerts());
    }

    @GetMapping("/alerts/low-stock")
    public List<Ingredient> getLowStockAlerts() {
        return inventoryService.getLowStockAlerts();
    }

    @PostMapping
    public Ingredient add(@Valid @RequestBody IngredientDto dto) {
        return inventoryService.add(dto);
    }

    @PutMapping("/{id}")
    public Ingredient update(@PathVariable Long id, @Valid @RequestBody IngredientDto dto) {
        return inventoryService.update(id, dto);
    }

    @PutMapping("/{id}/stock")
    public Ingredient updateStock(
            @PathVariable Long id,
            @RequestParam double qty,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate expiryDate,
            @RequestParam(required = false, defaultValue = "false") boolean addStock) {
        return inventoryService.updateStock(id, qty, expiryDate, addStock);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }

    @PostMapping("/generate-po")
    public PurchaseOrder generatePO() {
        return inventoryService.generatePOFromLowStock();
    }
}
