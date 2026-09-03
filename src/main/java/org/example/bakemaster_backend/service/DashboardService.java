package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.OrderItemRepository;
import org.example.bakemaster_backend.repository.OrderRepository;
import org.example.bakemaster_backend.repository.WasteLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardService {
    private final IngredientRepository ingredientRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final WasteLogRepository wasteLogRepo;

    public DashboardService(IngredientRepository ingredientRepo,
                            OrderRepository orderRepo,
                            OrderItemRepository orderItemRepo,
                            WasteLogRepository wasteLogRepo) {
        this.ingredientRepo = ingredientRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.wasteLogRepo = wasteLogRepo;
    }

    public Map<String, Object> getDashboard() {
        LocalDate today = LocalDate.now();

        // 1. Alert counts
        List<Ingredient> lowStock = ingredientRepo.findLowStockIngredients();
        List<Ingredient> expired = ingredientRepo.findExpiredIngredients();
        List<Ingredient> criticalExpiry = ingredientRepo.findExpiringWithinDays(3);
        List<Ingredient> warningExpiry = ingredientRepo.findExpiringWithinDays(7);

        long pendingOrders = orderRepo.countByStatus("RECEIVED") + orderRepo.countByStatus("PENDING");
        long inProgress = orderRepo.countByStatus("CONFIRMED") + orderRepo.countByStatus("IN_PROGRESS");
        long completedToday = orderRepo.countByStatus("COMPLETED");

        double todaySales = orderRepo.sumSalesByDate(today);
        long todayOrdersCount = orderRepo.countByOrderDate(today);

        // 2. Top-selling products
        List<Object[]> topSellersRaw = orderItemRepo.findTopSellingProducts();
        List<Map<String, Object>> topSellingProducts = new ArrayList<>();
        int limit = Math.min(topSellersRaw.size(), 5);
        for (int i = 0; i < limit; i++) {
            Object[] row = topSellersRaw.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("productName", row[0]);
            item.put("quantitySold", row[1]);
            item.put("totalRevenue", row[2]);
            topSellingProducts.add(item);
        }

        // 3. Last 7 Days Sales Trend (for chart)
        List<Map<String, Object>> weeklySales = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            double daySales = orderRepo.sumSalesByDate(day);
            long dayCount = orderRepo.countByOrderDate(day);

            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("date", day.toString());
            dayMap.put("dayOfWeek", day.getDayOfWeek().toString().substring(0, 3));
            dayMap.put("sales", daySales);
            dayMap.put("orderCount", dayCount);
            weeklySales.add(dayMap);
        }

        // 4. Waste summary this month
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.plusMonths(1).withDayOfMonth(1).minusDays(1);
        double monthlyWasteCost = wasteLogRepo.sumLossBetween(startOfMonth, endOfMonth);

        Map<String, Object> data = new HashMap<>();
        data.put("lowStockCount", lowStock.size());
        data.put("expiringSoonCount", criticalExpiry.size());
        data.put("expiredCount", expired.size());
        data.put("pendingOrders", pendingOrders);
        data.put("ordersInProgress", inProgress);
        data.put("completedToday", completedToday);
        data.put("todaySalesTotal", todaySales);
        data.put("todayOrdersCount", todayOrdersCount);

        data.put("lowStockAlerts", lowStock);
        data.put("expiringSoonAlerts", criticalExpiry);
        data.put("warningExpiryAlerts", warningExpiry);
        data.put("expiredAlerts", expired);

        data.put("topSellingProducts", topSellingProducts);
        data.put("weeklySalesTrend", weeklySales);
        data.put("monthlyWasteCost", monthlyWasteCost);

        return data;
    }
}
