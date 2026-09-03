package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.PurchaseOrder;
import org.example.bakemaster_backend.entity.PurchaseOrderItem;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepository poRepo;
    private final IngredientRepository ingredientRepo;

    public PurchaseOrderService(PurchaseOrderRepository poRepo, IngredientRepository ingredientRepo) {
        this.poRepo = poRepo;
        this.ingredientRepo = ingredientRepo;
    }

    public List<PurchaseOrder> getAll() {
        return poRepo.findAll();
    }

    public PurchaseOrder getById(Long id) {
        return poRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + id));
    }

    @Transactional
    public PurchaseOrder sendPurchaseOrder(Long id) {
        PurchaseOrder po = getById(id);
        po.setStatus("SENT");
        return poRepo.save(po);
    }

    @Transactional
    public PurchaseOrder receivePurchaseOrder(Long id) {
        PurchaseOrder po = getById(id);
        if ("RECEIVED".equalsIgnoreCase(po.getStatus())) {
            throw new RuntimeException("Purchase order #" + id + " has already been received into inventory");
        }

        // Auto-increment ingredient stock in inventory
        for (PurchaseOrderItem item : po.getItems()) {
            if (item.getIngredientId() != null) {
                ingredientRepo.findById(item.getIngredientId()).ifPresent(ingredient -> {
                    ingredient.setQuantity(ingredient.getQuantity() + item.getQuantity());
                    ingredientRepo.save(ingredient);
                });
            }
        }

        po.setStatus("RECEIVED");
        po.setReceivedDate(LocalDate.now());
        return poRepo.save(po);
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(org.example.bakemaster_backend.dto.PurchaseOrderCreateDto dto) {
        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(dto.getSupplierId());
        po.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDate.now());
        po.setStatus("PENDING");

        double totalAmount = 0.0;
        if (dto.getItems() != null) {
            for (org.example.bakemaster_backend.dto.PurchaseOrderCreateDto.PurchaseOrderItemInput itemInput : dto.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setIngredientId(itemInput.getIngredientId());
                item.setIngredientName(itemInput.getIngredientName());
                item.setQuantity(itemInput.getQuantity());
                item.setPurchaseOrder(po);
                po.getItems().add(item);
                totalAmount += itemInput.getQuantity() * itemInput.getUnitPrice();
            }
        }
        po.setTotalAmount(totalAmount);
        return poRepo.save(po);
    }

    @Transactional
    public PurchaseOrder cancelPurchaseOrder(Long id) {
        PurchaseOrder po = getById(id);
        if ("RECEIVED".equalsIgnoreCase(po.getStatus())) {
            throw new RuntimeException("Cannot cancel a purchase order that has already been received.");
        }
        po.setStatus("CANCELLED");
        return poRepo.save(po);
    }

    public void deletePurchaseOrder(Long id) {
        poRepo.deleteById(id);
    }
}
