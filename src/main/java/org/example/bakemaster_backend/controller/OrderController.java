package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.OrderCreateDto;
import org.example.bakemaster_backend.dto.UpdateStatusDto;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @GetMapping
    public List<OrderEntity> getAll() { return orderService.getAllOrders(); }

    @PostMapping
    public OrderEntity create(@Valid @RequestBody OrderCreateDto dto) {
        return orderService.createOrder(dto);
    }

    @PutMapping("/{id}/status")
    public OrderEntity updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusDto dto) {
        return orderService.updateStatus(id, dto);
    }

    @GetMapping("/queue")
    public List<OrderEntity> productionQueue() { return orderService.getProductionQueue(); }
}
