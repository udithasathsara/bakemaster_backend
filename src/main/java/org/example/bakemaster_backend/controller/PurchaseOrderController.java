package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.entity.PurchaseOrder;
import org.example.bakemaster_backend.service.PurchaseOrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseOrder> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PurchaseOrder getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public PurchaseOrder createPurchaseOrder(@Valid @RequestBody org.example.bakemaster_backend.dto.PurchaseOrderCreateDto dto) {
        return service.createPurchaseOrder(dto);
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public PurchaseOrder sendPurchaseOrder(@PathVariable Long id) {
        return service.sendPurchaseOrder(id);
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public PurchaseOrder receivePurchaseOrder(@PathVariable Long id) {
        return service.receivePurchaseOrder(id);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public PurchaseOrder cancelPurchaseOrder(@PathVariable Long id) {
        return service.cancelPurchaseOrder(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public void deletePurchaseOrder(@PathVariable Long id) {
        service.deletePurchaseOrder(id);
    }
}
