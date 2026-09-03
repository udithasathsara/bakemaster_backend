package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.ProductionTaskDto;
import org.example.bakemaster_backend.entity.ProductionTask;
import org.example.bakemaster_backend.service.ProductionTaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/production-tasks")
public class ProductionTaskController {

    private final ProductionTaskService taskService;

    public ProductionTaskController(ProductionTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<ProductionTask> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/today")
    public List<ProductionTask> getTodaySchedule() {
        return taskService.getTodaySchedule();
    }

    @GetMapping("/order/{orderId}")
    public List<ProductionTask> getTasksByOrderId(@PathVariable Long orderId) {
        return taskService.getTasksByOrderId(orderId);
    }

    @GetMapping("/range")
    public List<ProductionTask> getTasksByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return taskService.getTasksByDateRange(start, end);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ProductionTask createTask(@Valid @RequestBody ProductionTaskDto dto) {
        return taskService.createTask(dto);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ProductionTask assignStaff(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return taskService.assignStaff(id, body.get("staffId"));
    }

    @PutMapping("/{id}/status")
    public ProductionTask updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return taskService.updateStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
