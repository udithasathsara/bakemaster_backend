package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.IngredientDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.PurchaseOrder;
import org.example.bakemaster_backend.entity.PurchaseOrderItem;
import org.example.bakemaster_backend.entity.Supplier;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.PurchaseOrderRepository;
import org.example.bakemaster_backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {
    private final IngredientRepository ingredientRepo;
    private final SupplierRepository supplierRepo;
    private final PurchaseOrderRepository poRepo;

    public InventoryService(IngredientRepository ingredientRepo, SupplierRepository supplierRepo, PurchaseOrderRepository poRepo) {
        this.ingredientRepo = ingredientRepo;
        this.supplierRepo = supplierRepo;
        this.poRepo = poRepo;
    }

    public List<Ingredient> getAll() { return ingredientRepo.findAll(); }

    public Ingredient add(IngredientDto dto) {
        Ingredient ing = new Ingredient();
        ing.setName(dto.getName());
        ing.setQuantity(dto.getQuantity());
        ing.setUnit(dto.getUnit());
        ing.setExpiryDate(dto.getExpiryDate());
        ing.setReorderThreshold(dto.getReorderThreshold());
        return ingredientRepo.save(ing);
    }

    public Ingredient updateStock(Long id, double newQty) {
        Ingredient ing = ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        ing.setQuantity(newQty);
        return ingredientRepo.save(ing);
    }

    public void delete(Long id) { ingredientRepo.deleteById(id); }

    @Transactional
    public PurchaseOrder generatePOFromLowStock() {
        List<Ingredient> lowStock = ingredientRepo.findByQuantityLessThan(5.0);
        if (lowStock.isEmpty()) throw new RuntimeException("No low stock items");
        Supplier defaultSupplier = supplierRepo.findAll().get(0); // pick first supplier
        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(defaultSupplier.getId());
        po.setOrderDate(LocalDate.now());
        po.setStatus("PENDING");
        for (Ingredient ing : lowStock) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setIngredientId(ing.getId());
            item.setIngredientName(ing.getName());
            item.setQuantity(ing.getReorderThreshold() * 2); // order double threshold
            item.setPurchaseOrder(po);
            po.getItems().add(item);
        }
        return poRepo.save(po);
    }
}
