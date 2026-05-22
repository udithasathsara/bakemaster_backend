package org.example.bakemaster_backend.service;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final IngredientRepository ingredientRepo;
    private final OrderRepository orderRepo;

    public DashboardService(IngredientRepository ingredientRepo, OrderRepository orderRepo) {
        this.ingredientRepo = ingredientRepo;
        this.orderRepo = orderRepo;
    }

    public Map<String, Object> getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate weekLater = today.plusDays(7);
        List<Ingredient> lowStock = ingredientRepo.findByQuantityLessThan(5.0); // threshold for demo
        List<Ingredient> expiringSoon = ingredientRepo.findByExpiryDateBefore(weekLater);
        long pendingOrders = orderRepo.countByStatus("PENDING");
        long inProgress = orderRepo.countByStatus("IN_PROGRESS");

        Map<String, Object> data = new HashMap<>();
        data.put("lowStockAlerts", lowStock);
        data.put("expiryAlerts", expiringSoon);
        data.put("pendingOrders", pendingOrders);
        data.put("ordersInProgress", inProgress);
        return data;
    }
}
