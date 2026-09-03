package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.OrderCreateDto;
import org.example.bakemaster_backend.dto.OrderItemDto;
import org.example.bakemaster_backend.dto.UpdateStatusDto;
import org.example.bakemaster_backend.entity.*;
import org.example.bakemaster_backend.repository.CustomerRepository;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.OrderRepository;
import org.example.bakemaster_backend.repository.ProductRepository;
import org.example.bakemaster_backend.repository.ProductionTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final IngredientRepository ingredientRepo;
    private final ProductionTaskService productionTaskService;
    private final ProductionTaskRepository productionTaskRepo;

    public OrderService(OrderRepository orderRepo,
                        CustomerRepository customerRepo,
                        ProductRepository productRepo,
                        IngredientRepository ingredientRepo,
                        ProductionTaskService productionTaskService,
                        ProductionTaskRepository productionTaskRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.ingredientRepo = ingredientRepo;
        this.productionTaskService = productionTaskService;
        this.productionTaskRepo = productionTaskRepo;
    }

    @Transactional
    public OrderEntity createOrder(OrderCreateDto dto) {
        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + dto.getCustomerId()));

        OrderEntity order = new OrderEntity();
        order.setCustomerId(customer.getId());
        order.setChannel(dto.getChannel() != null ? dto.getChannel().toUpperCase() : "WALK_IN");
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDeliveryDeadline(dto.getDeliveryDeadline() != null ? dto.getDeliveryDeadline() : LocalDate.now());
        order.setOrderDate(LocalDate.now());
        order.setStatus("RECEIVED");
        order.setPaymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus().toUpperCase() : "PENDING");
        order.setNotes(dto.getNotes());

        double totalAmount = 0.0;

        for (OrderItemDto itemDto : dto.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());

            double unitPrice = itemDto.getUnitPrice();
            if (itemDto.getProductId() != null) {
                item.setProductId(itemDto.getProductId());
                Optional<Product> prodOpt = productRepo.findById(itemDto.getProductId());
                if (prodOpt.isPresent() && unitPrice <= 0) {
                    unitPrice = prodOpt.get().getSellingPrice();
                }
            } else {
                // Try matching by product name if productId was not specified
                Optional<Product> prodOpt = productRepo.findByNameIgnoreCase(itemDto.getProductName());
                prodOpt.ifPresent(p -> {
                    item.setProductId(p.getId());
                });
            }

            item.setUnitPrice(unitPrice);
            double subtotal = unitPrice * itemDto.getQuantity();
            item.setSubtotal(subtotal);
            item.setSpecialInstructions(itemDto.getSpecialInstructions());

            totalAmount += subtotal;
            order.getItems().add(item);
        }

        order.setTotalAmount(totalAmount);

        // Loyalty points: 10 points per item + 1 point per $10 spent
        int earnedPoints = (10 * dto.getItems().size()) + (int) (totalAmount / 10);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + earnedPoints);
        customerRepo.save(customer);

        return orderRepo.save(order);
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepo.findAll();
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public OrderEntity confirmOrder(Long id) {
        OrderEntity order = getOrderById(id);
        String currentStatus = order.getStatus().toUpperCase();

        if (!currentStatus.equals("RECEIVED") && !currentStatus.equals("PENDING")) {
            throw new RuntimeException("Order #" + id + " cannot be confirmed because it is currently in status: " + currentStatus);
        }

        // 1. Check stock availability for all ingredients via recipes
        Map<Long, Double> requiredIngredients = calculateRequiredIngredients(order);

        for (Map.Entry<Long, Double> entry : requiredIngredients.entrySet()) {
            Long ingredientId = entry.getKey();
            Double neededQty = entry.getValue();

            Ingredient ingredient = ingredientRepo.findById(ingredientId)
                    .orElseThrow(() -> new RuntimeException("Required ingredient id: " + ingredientId + " not found in database"));

            if (ingredient.getQuantity() < neededQty) {
                throw new RuntimeException("Insufficient stock for ingredient: " + ingredient.getName() +
                        ". In stock: " + ingredient.getQuantity() + " " + ingredient.getUnit() +
                        ", Required: " + neededQty + " " + ingredient.getUnit());
            }
        }

        // 2. Deduct/Reserve ingredients from inventory
        for (Map.Entry<Long, Double> entry : requiredIngredients.entrySet()) {
            Ingredient ingredient = ingredientRepo.findById(entry.getKey()).get();
            ingredient.setQuantity(ingredient.getQuantity() - entry.getValue());
            ingredientRepo.save(ingredient);
        }

        // 3. Update status to CONFIRMED
        order.setStatus("CONFIRMED");
        OrderEntity savedOrder = orderRepo.save(order);

        // 4. Automatically generate production tasks for the kitchen
        productionTaskService.generateTasksForOrder(savedOrder);

        return savedOrder;
    }

    @Transactional
    public OrderEntity cancelOrder(Long id) {
        OrderEntity order = getOrderById(id);
        String currentStatus = order.getStatus().toUpperCase();

        if (currentStatus.equals("COMPLETED") || currentStatus.equals("DELIVERED")) {
            throw new RuntimeException("Order #" + id + " cannot be cancelled because it is already " + currentStatus);
        }

        if (currentStatus.equals("CANCELLED")) {
            return order;
        }

        // If order was CONFIRMED or IN_PROGRESS, restore deducted ingredients
        if (currentStatus.equals("CONFIRMED") || currentStatus.equals("IN_PROGRESS")) {
            Map<Long, Double> requiredIngredients = calculateRequiredIngredients(order);
            for (Map.Entry<Long, Double> entry : requiredIngredients.entrySet()) {
                ingredientRepo.findById(entry.getKey()).ifPresent(ingredient -> {
                    ingredient.setQuantity(ingredient.getQuantity() + entry.getValue());
                    ingredientRepo.save(ingredient);
                });
            }

            // Cancel any associated production tasks
            List<ProductionTask> tasks = productionTaskRepo.findByOrderId(order.getId());
            for (ProductionTask task : tasks) {
                if (!"COMPLETED".equalsIgnoreCase(task.getStatus())) {
                    task.setStatus("CANCELLED");
                    productionTaskRepo.save(task);
                }
            }
        }

        order.setStatus("CANCELLED");
        return orderRepo.save(order);
    }

    @Transactional
    public OrderEntity updateStatus(Long id, UpdateStatusDto dto) {
        OrderEntity order = getOrderById(id);
        String targetStatus = dto.getStatus().toUpperCase();
        String currentStatus = order.getStatus().toUpperCase();

        if (currentStatus.equals(targetStatus)) {
            return order;
        }

        // If target is CONFIRMED, execute confirmation logic (stock check & task generation)
        if (targetStatus.equals("CONFIRMED")) {
            return confirmOrder(id);
        }

        // If target is CANCELLED, execute cancellation logic (stock rollback & task cancel)
        if (targetStatus.equals("CANCELLED")) {
            return cancelOrder(id);
        }

        // State Machine validation for standard transitions
        validateTransition(currentStatus, targetStatus);

        order.setStatus(targetStatus);
        return orderRepo.save(order);
    }

    private void validateTransition(String current, String target) {
        // Allowed transitions:
        // RECEIVED / PENDING -> CONFIRMED, CANCELLED
        // CONFIRMED -> IN_PROGRESS, CANCELLED
        // IN_PROGRESS -> COMPLETED (or BAKING for legacy)
        // COMPLETED -> DELIVERED
        boolean valid = false;

        switch (current) {
            case "RECEIVED":
            case "PENDING":
                valid = target.equals("CONFIRMED") || target.equals("CANCELLED") || target.equals("IN_PROGRESS");
                break;
            case "CONFIRMED":
                valid = target.equals("IN_PROGRESS") || target.equals("BAKING") || target.equals("CANCELLED");
                break;
            case "IN_PROGRESS":
            case "BAKING":
                valid = target.equals("COMPLETED") || target.equals("DELIVERED");
                break;
            case "COMPLETED":
                valid = target.equals("DELIVERED");
                break;
            default:
                valid = false;
        }

        if (!valid) {
            throw new RuntimeException("Invalid status transition from " + current + " to " + target);
        }
    }

    private Map<Long, Double> calculateRequiredIngredients(OrderEntity order) {
        Map<Long, Double> ingredientTotals = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            Product product = null;
            if (item.getProductId() != null) {
                product = productRepo.findById(item.getProductId()).orElse(null);
            }
            if (product == null && item.getProductName() != null) {
                product = productRepo.findByNameIgnoreCase(item.getProductName()).orElse(null);
            }

            if (product != null && product.getRecipeItems() != null) {
                for (RecipeItem recipeItem : product.getRecipeItems()) {
                    Long ingId = recipeItem.getIngredient().getId();
                    double needed = recipeItem.getQuantityRequired() * item.getQuantity();
                    ingredientTotals.put(ingId, ingredientTotals.getOrDefault(ingId, 0.0) + needed);
                }
            }
        }
        return ingredientTotals;
    }

    public List<OrderEntity> getProductionQueue() {
        return orderRepo.findByStatusNotIn(Arrays.asList("DELIVERED", "CANCELLED"));
    }
}
