package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.ProductionTaskDto;
import org.example.bakemaster_backend.entity.OrderEntity;
import org.example.bakemaster_backend.entity.OrderItem;
import org.example.bakemaster_backend.entity.ProductionTask;
import org.example.bakemaster_backend.entity.Staff;
import org.example.bakemaster_backend.repository.OrderRepository;
import org.example.bakemaster_backend.repository.ProductionTaskRepository;
import org.example.bakemaster_backend.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductionTaskService {
    private final ProductionTaskRepository taskRepo;
    private final OrderRepository orderRepo;
    private final StaffRepository staffRepo;

    public ProductionTaskService(ProductionTaskRepository taskRepo, OrderRepository orderRepo, StaffRepository staffRepo) {
        this.taskRepo = taskRepo;
        this.orderRepo = orderRepo;
        this.staffRepo = staffRepo;
    }

    public List<ProductionTask> getAllTasks() {
        return taskRepo.findAll();
    }

    public List<ProductionTask> getTasksByOrderId(Long orderId) {
        return taskRepo.findByOrderId(orderId);
    }

    public List<ProductionTask> getTodaySchedule() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return taskRepo.findByScheduledStartBetween(startOfDay, endOfDay);
    }

    public List<ProductionTask> getTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        return taskRepo.findByScheduledStartBetween(start, end);
    }

    @Transactional
    public List<ProductionTask> generateTasksForOrder(OrderEntity order) {
        List<ProductionTask> generatedTasks = new ArrayList<>();
        List<Staff> bakers = staffRepo.findByRole("BAKER");
        List<Staff> decorators = staffRepo.findByRole("DECORATOR");

        Long defaultBakerId = !bakers.isEmpty() ? bakers.get(0).getId() : null;
        Long defaultDecoratorId = !decorators.isEmpty() ? decorators.get(0).getId() : null;

        LocalDateTime baseTime = LocalDateTime.now();
        if (order.getDeliveryDeadline() != null) {
            // Target completing before deadline
            baseTime = order.getDeliveryDeadline().atTime(12, 0).minusHours(4);
            if (baseTime.isBefore(LocalDateTime.now())) {
                baseTime = LocalDateTime.now().plusMinutes(15);
            }
        }

        int offsetMinutes = 0;

        for (OrderItem item : order.getItems()) {
            String nameLower = item.getProductName().toLowerCase();
            boolean isDecorated = nameLower.contains("cake") || nameLower.contains("cupcake");

            // 1. Baking Task
            ProductionTask bakingTask = new ProductionTask();
            bakingTask.setOrderId(order.getId());
            bakingTask.setOrderItemId(item.getId());
            bakingTask.setProductName(item.getProductName());
            bakingTask.setQuantity(item.getQuantity());
            bakingTask.setTaskType("BAKING");
            bakingTask.setStatus("PENDING");
            bakingTask.setPriority(1);
            bakingTask.setAssignedStaffId(defaultBakerId);
            bakingTask.setScheduledStart(baseTime.plusMinutes(offsetMinutes));
            bakingTask.setScheduledEnd(baseTime.plusMinutes(offsetMinutes + 90));
            bakingTask.setNotes("Bake " + item.getQuantity() + "x " + item.getProductName());
            generatedTasks.add(taskRepo.save(bakingTask));

            offsetMinutes += 90;

            // 2. Decorating Task (if applicable)
            if (isDecorated) {
                ProductionTask decoratingTask = new ProductionTask();
                decoratingTask.setOrderId(order.getId());
                decoratingTask.setOrderItemId(item.getId());
                decoratingTask.setProductName(item.getProductName());
                decoratingTask.setQuantity(item.getQuantity());
                decoratingTask.setTaskType("DECORATING");
                decoratingTask.setStatus("PENDING");
                decoratingTask.setPriority(2);
                decoratingTask.setAssignedStaffId(defaultDecoratorId);
                decoratingTask.setScheduledStart(baseTime.plusMinutes(offsetMinutes));
                decoratingTask.setScheduledEnd(baseTime.plusMinutes(offsetMinutes + 60));
                decoratingTask.setNotes(item.getSpecialInstructions() != null ? item.getSpecialInstructions() : "Decorate and finish");
                generatedTasks.add(taskRepo.save(decoratingTask));

                offsetMinutes += 60;
            }

            // 3. Packaging Task
            ProductionTask packagingTask = new ProductionTask();
            packagingTask.setOrderId(order.getId());
            packagingTask.setOrderItemId(item.getId());
            packagingTask.setProductName(item.getProductName());
            packagingTask.setQuantity(item.getQuantity());
            packagingTask.setTaskType("PACKAGING");
            packagingTask.setStatus("PENDING");
            packagingTask.setPriority(3);
            packagingTask.setScheduledStart(baseTime.plusMinutes(offsetMinutes));
            packagingTask.setScheduledEnd(baseTime.plusMinutes(offsetMinutes + 30));
            packagingTask.setNotes("Box, label and prepare for order #" + order.getId());
            generatedTasks.add(taskRepo.save(packagingTask));

            offsetMinutes += 30;
        }

        return generatedTasks;
    }

    private void validateNoStaffConflict(Long staffId, LocalDateTime start, LocalDateTime end, Long taskId) {
        if (staffId == null || start == null || end == null) return;
        List<ProductionTask> conflicts = taskRepo.findConflictingTasks(staffId, start, end, taskId);
        if (!conflicts.isEmpty()) {
            ProductionTask conflict = conflicts.get(0);
            String staffName = staffRepo.findById(staffId).map(Staff::getName).orElse("Staff #" + staffId);
            String taskName = conflict.getProductName() != null ? conflict.getProductName() : conflict.getTaskType();
            String startTime = conflict.getScheduledStart() != null ? conflict.getScheduledStart().toLocalTime().toString().substring(0, 5) : "";
            String endTime = conflict.getScheduledEnd() != null ? conflict.getScheduledEnd().toLocalTime().toString().substring(0, 5) : "";
            throw new RuntimeException("Scheduling conflict: " + staffName + " is already assigned to task '" + taskName + " (" + conflict.getTaskType() + ")' from " + startTime + " to " + endTime + ". Same member cannot be assigned to two tasks at the same time.");
        }
    }

    public ProductionTask createTask(ProductionTaskDto dto) {
        if (dto.getScheduledStart() != null && dto.getScheduledEnd() != null && dto.getScheduledEnd().isBefore(dto.getScheduledStart())) {
            throw new RuntimeException("Scheduled end time cannot be before scheduled start time.");
        }
        if (dto.getAssignedStaffId() != null) {
            validateNoStaffConflict(dto.getAssignedStaffId(), dto.getScheduledStart(), dto.getScheduledEnd(), null);
        }

        ProductionTask task = new ProductionTask();
        task.setOrderId(dto.getOrderId());
        task.setOrderItemId(dto.getOrderItemId());

        String prodName = dto.getProductName();
        if ((prodName == null || prodName.isBlank()) && dto.getOrderId() != null) {
            prodName = orderRepo.findById(dto.getOrderId())
                    .map(o -> o.getItems().isEmpty() ? "Order #" + o.getId() : o.getItems().get(0).getProductName())
                    .orElse("Order #" + dto.getOrderId());
        }
        task.setProductName(prodName != null ? prodName : dto.getTaskType() + " Task");
        task.setQuantity(dto.getQuantity() > 0 ? dto.getQuantity() : 1);
        task.setTaskType(dto.getTaskType());
        task.setAssignedStaffId(dto.getAssignedStaffId());
        task.setScheduledStart(dto.getScheduledStart());
        task.setScheduledEnd(dto.getScheduledEnd());
        task.setPriority(dto.getPriority());
        task.setNotes(dto.getNotes());
        task.setStatus("PENDING");
        return taskRepo.save(task);
    }

    public ProductionTask assignStaff(Long taskId, Long staffId) {
        ProductionTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        if (staffId != null) {
            validateNoStaffConflict(staffId, task.getScheduledStart(), task.getScheduledEnd(), taskId);
        }
        task.setAssignedStaffId(staffId);
        return taskRepo.save(task);
    }

    @Transactional
    public ProductionTask updateStatus(Long taskId, String status) {
        ProductionTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        task.setStatus(status);
        if ("IN_PROGRESS".equalsIgnoreCase(status)) {
            task.setActualStart(LocalDateTime.now());
            // If parent order is still CONFIRMED, advance it to IN_PROGRESS
            if (task.getOrderId() != null) {
                orderRepo.findById(task.getOrderId()).ifPresent(order -> {
                    if ("CONFIRMED".equalsIgnoreCase(order.getStatus())) {
                        order.setStatus("IN_PROGRESS");
                        orderRepo.save(order);
                    }
                });
            }
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            task.setActualEnd(LocalDateTime.now());
            taskRepo.save(task);

            // Check if all tasks for this order are completed
            if (task.getOrderId() != null) {
                long remainingUnfinished = taskRepo.countByOrderIdAndStatusNot(task.getOrderId(), "COMPLETED");
                if (remainingUnfinished == 0) {
                    orderRepo.findById(task.getOrderId()).ifPresent(order -> {
                        order.setStatus("COMPLETED");
                        orderRepo.save(order);
                    });
                }
            }
            return task;
        }

        return taskRepo.save(task);
    }

    public void deleteTask(Long id) {
        taskRepo.deleteById(id);
    }
}
