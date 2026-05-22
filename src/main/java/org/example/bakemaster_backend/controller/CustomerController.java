package org.example.bakemaster_backend.controller;

import org.example.bakemaster_backend.entity.Customer;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }
    @GetMapping
    public List<Customer> getAll() { return service.getAll(); }
    @GetMapping("/{id}/orders")
    public List<OrderEntity> getOrders(@PathVariable Long id) { return service.getCustomerOrders(id); }
}
