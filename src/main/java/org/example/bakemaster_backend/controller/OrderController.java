package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.OrderCreateDto;
import org.example.bakemaster_backend.dto.UpdateStatusDto;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderEntity> getAll() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderEntity getById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public ResponseEntity<OrderEntity> create(@Valid @RequestBody OrderCreateDto dto) {
        OrderEntity created = orderService.createOrder(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/confirm")
    public OrderEntity confirmOrder(@PathVariable Long id) {
        return orderService.confirmOrder(id);
    }

    @PostMapping("/{id}/cancel")
    public OrderEntity cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PutMapping("/{id}/status")
    public OrderEntity updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusDto dto) {
        return orderService.updateStatus(id, dto);
    }

    @GetMapping("/queue")
    public List<OrderEntity> productionQueue() {
        return orderService.getProductionQueue();
    }
}
