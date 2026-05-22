package org.example.bakemaster_backend.service;
import org.example.bakemaster_backend.dto.OrderCreateDto;
import org.example.bakemaster_backend.dto.UpdateStatusDto;
import org.example.bakemaster_backend.entity.Customer;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.entity.OrderItem;
import org.example.bakemaster_backend.repository.CustomerRepository;
import org.example.bakemaster_backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;

    public OrderService(OrderRepository orderRepo, CustomerRepository customerRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }

    @Transactional
    public OrderEntity createOrder(OrderCreateDto dto) {
        OrderEntity order = new OrderEntity();
        order.setCustomerId(dto.getCustomerId());
        order.setChannel(dto.getChannel());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDeliveryDeadline(dto.getDeliveryDeadline());
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");

        for (var itemDto : dto.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            item.setOrder(order);
            order.getItems().add(item);
        }

        // loyalty points: 10 per item
        Customer customer = customerRepo.findById(dto.getCustomerId()).orElse(null);
        if (customer != null) {
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + 10 * dto.getItems().size());
            customerRepo.save(customer);
        }

        return orderRepo.save(order);
    }

    public List<OrderEntity> getAllOrders() { return orderRepo.findAll(); }

    public OrderEntity updateStatus(Long id, UpdateStatusDto dto) {
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(dto.getStatus());
        return orderRepo.save(order);
    }

    public List<OrderEntity> getProductionQueue() {
        return orderRepo.findByStatusNot("DELIVERED");
    }
}
