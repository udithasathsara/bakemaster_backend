package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.WasteLogDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.WasteLog;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.WasteLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class WasteLogService {

    private final WasteLogRepository wasteLogRepo;
    private final IngredientRepository ingredientRepo;

    public WasteLogService(WasteLogRepository wasteLogRepo, IngredientRepository ingredientRepo) {
        this.wasteLogRepo = wasteLogRepo;
        this.ingredientRepo = ingredientRepo;
    }

    public List<WasteLog> getAll() {
        return wasteLogRepo.findAll();
    }

    public List<WasteLog> getByRange(LocalDate start, LocalDate end) {
        return wasteLogRepo.findByWasteDateBetween(start, end);
    }

    @Transactional
    public WasteLog logWaste(WasteLogDto dto) {
        WasteLog log = new WasteLog();
        log.setIngredientId(dto.getIngredientId());
        log.setIngredientName(dto.getIngredientName());
        log.setQuantity(dto.getQuantity());
        log.setReason(dto.getReason() != null ? dto.getReason().trim().toUpperCase() : "OTHER");
        log.setWasteDate(dto.getWasteDate() != null ? dto.getWasteDate() : LocalDate.now());
        log.setLoggedBy(dto.getLoggedBy() != null && !dto.getLoggedBy().isBlank() ? dto.getLoggedBy() : "Staff");
        log.setNotes(dto.getNotes());

        double costLoss = dto.getEstimatedCostLoss();
        String unit = dto.getUnit();

        // If an ingredient is specified by ID or name, auto-deduct stock and calculate financial loss
        Optional<Ingredient> ingOpt = Optional.empty();
        if (dto.getIngredientId() != null && dto.getIngredientId() > 0) {
            ingOpt = ingredientRepo.findById(dto.getIngredientId());
        } else if (dto.getIngredientName() != null && !dto.getIngredientName().isBlank()) {
            ingOpt = ingredientRepo.findByNameIgnoreCase(dto.getIngredientName().trim());
        }

        if (ingOpt.isPresent()) {
            Ingredient ing = ingOpt.get();
            log.setIngredientId(ing.getId());
            log.setIngredientName(ing.getName());
            if (unit == null || unit.isBlank()) {
                unit = ing.getUnit();
            }
            if (costLoss <= 0) {
                costLoss = dto.getQuantity() * ing.getCostPerUnit();
            }
            // Deduct from stock
            double remaining = Math.max(0.0, ing.getQuantity() - dto.getQuantity());
            ing.setQuantity(remaining);
            ingredientRepo.save(ing);
        }

        log.setUnit(unit != null ? unit : "unit");
        log.setEstimatedCostLoss(Math.max(0.0, costLoss));

        return wasteLogRepo.save(log);
    }

    public Map<String, Object> getWasteAnalytics() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);

        double monthLoss = wasteLogRepo.sumLossBetween(startOfMonth, endOfMonth);
        List<Object[]> reasonSummary = wasteLogRepo.getWasteSummaryByReason();

        List<Map<String, Object>> breakdown = new ArrayList<>();
        for (Object[] row : reasonSummary) {
            Map<String, Object> item = new HashMap<>();
            item.put("reason", row[0]);
            item.put("count", row[1]);
            item.put("totalLoss", row[2]);
            breakdown.add(item);
        }

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("currentMonthTotalLoss", monthLoss);
        analytics.put("totalRecordedLogs", wasteLogRepo.count());
        analytics.put("reasonBreakdown", breakdown);
        return analytics;
    }
}
