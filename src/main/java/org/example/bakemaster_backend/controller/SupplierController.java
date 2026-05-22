package org.example.bakemaster_backend.controller;

import org.example.bakemaster_backend.entity.Supplier;
import org.example.bakemaster_backend.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService service;
    public SupplierController(SupplierService service) { this.service = service; }

    @GetMapping
    public List<Supplier> getAll() { return service.getAll(); }

    @PostMapping
    public Supplier add(@RequestBody Supplier supplier) { return service.add(supplier); }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        return service.add(supplier);  // reuses save
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
