package org.example.bakemaster_backend.controller;

import org.example.bakemaster_backend.entity.Customer;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.service.CustomerService;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public Customer add(@RequestBody Customer customer) {
        return service.add(customer);
    }
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        return service.add(customer);   // reuse save
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
