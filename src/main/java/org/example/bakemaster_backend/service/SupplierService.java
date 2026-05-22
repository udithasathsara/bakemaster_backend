package org.example.bakemaster_backend.service;


import org.example.bakemaster_backend.entity.Supplier;
import org.example.bakemaster_backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository repo;
    public SupplierService(SupplierRepository repo) { this.repo = repo; }
    public List<Supplier> getAll() { return repo.findAll(); }
    public Supplier add(Supplier supplier) { return repo.save(supplier); }
}
