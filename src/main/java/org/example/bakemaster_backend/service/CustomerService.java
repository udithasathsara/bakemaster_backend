package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.entity.Customer;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.repository.CustomerRepository;
import org.example.bakemaster_backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    public CustomerService(CustomerRepository customerRepo, OrderRepository orderRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }
    public List<Customer> getAll() { return customerRepo.findAll(); }
    public List<OrderEntity> getCustomerOrders(Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }
    public Customer add(Customer customer) {
        return customerRepo.save(customer);
    }

    public void delete(Long id) {
        customerRepo.deleteById(id);
    }
}