package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.IngredientDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.PurchaseOrder;
import org.example.bakemaster_backend.entity.PurchaseOrderItem;
import org.example.bakemaster_backend.entity.Supplier;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.PurchaseOrderRepository;
import org.example.bakemaster_backend.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final IngredientRepository ingredientRepo;
    private final SupplierRepository supplierRepo;
    private final PurchaseOrderRepository poRepo;

    public InventoryService(IngredientRepository ingredientRepo, SupplierRepository supplierRepo, PurchaseOrderRepository poRepo) {
        this.ingredientRepo = ingredientRepo;
        this.supplierRepo = supplierRepo;
        this.poRepo = poRepo;
    }

    public List<Ingredient> getAll() {
        return ingredientRepo.findAll();
    }

    public Ingredient getById(Long id) {
        return ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
    }

    public Ingredient add(IngredientDto dto) {
        Ingredient ing = new Ingredient();
        ing.setName(dto.getName());
        ing.setQuantity(dto.getQuantity());
        ing.setUnit(dto.getUnit());
        ing.setExpiryDate(dto.getExpiryDate());
        ing.setReorderThreshold(dto.getReorderThreshold());
        ing.setCostPerUnit(dto.getCostPerUnit());
        return ingredientRepo.save(ing);
    }

    public Ingredient updateStock(Long id, double qty, LocalDate expiryDate, boolean addStock) {
        Ingredient ing = getById(id);
        if (addStock) {
            ing.setQuantity(Math.max(0.0, ing.getQuantity() + qty));
        } else {
            ing.setQuantity(Math.max(0.0, qty));
        }
        if (expiryDate != null) {
            ing.setExpiryDate(expiryDate);
        }
        return ingredientRepo.save(ing);
    }

    public Ingredient updateStock(Long id, double newQty) {
        return updateStock(id, newQty, null, false);
    }

    public Ingredient update(Long id, IngredientDto dto) {
        Ingredient ing = getById(id);
        ing.setName(dto.getName());
        ing.setQuantity(dto.getQuantity());
        ing.setUnit(dto.getUnit());
        ing.setExpiryDate(dto.getExpiryDate());
        ing.setReorderThreshold(dto.getReorderThreshold());
        if (dto.getCostPerUnit() > 0) {
            ing.setCostPerUnit(dto.getCostPerUnit());
        }
        return ingredientRepo.save(ing);
    }

    public void delete(Long id) {
        ingredientRepo.deleteById(id);
    }

    /**
     * Daily morning background scan running at 06:00 AM as specified in report section 4.3.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void performDailyExpiryAndStockScan() {
        log.info("Running daily BakeMaster morning inventory & expiry scan...");
        List<Ingredient> expired = ingredientRepo.findExpiredIngredients();
        List<Ingredient> critical = ingredientRepo.findExpiringWithinDays(3);
        List<Ingredient> lowStock = ingredientRepo.findLowStockIngredients();

        log.info("Morning Scan Summary: {} expired items, {} expiring within 3 days, {} low stock items",
                expired.size(), critical.size(), lowStock.size());
    }

    public Map<String, Object> getExpiryAlerts() {
        List<Ingredient> expired = ingredientRepo.findExpiredIngredients();
        List<Ingredient> critical = ingredientRepo.findExpiringWithinDays(3);
        List<Ingredient> warning = ingredientRepo.findExpiringWithinDays(7);

        Map<String, Object> alerts = new HashMap<>();
        alerts.put("expired", expired);
        alerts.put("criticalWithin3Days", critical);
        alerts.put("warningWithin7Days", warning);
        return alerts;
    }

    public List<Ingredient> getLowStockAlerts() {
        return ingredientRepo.findLowStockIngredients();
    }

    @Transactional
    public PurchaseOrder generatePOFromLowStock() {
        List<Ingredient> lowStock = ingredientRepo.findLowStockIngredients();
        if (lowStock.isEmpty()) {
            throw new RuntimeException("No ingredients are currently below reorder threshold");
        }

        List<Supplier> suppliers = supplierRepo.findAll();
        if (suppliers.isEmpty()) {
            throw new RuntimeException("Cannot generate PO: No suppliers registered in system");
        }

        Supplier supplier = suppliers.get(0);

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(supplier.getId());
        po.setOrderDate(LocalDate.now());
        po.setStatus("PENDING");

        for (Ingredient ing : lowStock) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setIngredientId(ing.getId());
            item.setIngredientName(ing.getName());
            // Suggest ordering difference or 2x threshold
            double orderQuantity = Math.max(ing.getReorderThreshold() * 2, (ing.getReorderThreshold() * 2) - ing.getQuantity());
            item.setQuantity(orderQuantity);
            item.setPurchaseOrder(po);
            po.getItems().add(item);
        }

        return poRepo.save(po);
    }
}
