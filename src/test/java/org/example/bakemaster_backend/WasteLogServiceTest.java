package org.example.bakemaster_backend;

import org.example.bakemaster_backend.dto.WasteLogDto;
import org.example.bakemaster_backend.entity.Ingredient;
import org.example.bakemaster_backend.entity.WasteLog;
import org.example.bakemaster_backend.repository.IngredientRepository;
import org.example.bakemaster_backend.repository.WasteLogRepository;
import org.example.bakemaster_backend.service.WasteLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WasteLogServiceTest {

    @Mock
    private WasteLogRepository wasteLogRepo;

    @Mock
    private IngredientRepository ingredientRepo;

    @InjectMocks
    private WasteLogService wasteLogService;

    private Ingredient milk;

    @BeforeEach
    void setUp() {
        milk = new Ingredient();
        milk.setId(5L);
        milk.setName("Milk");
        milk.setQuantity(10.0);
        milk.setUnit("litre");
        milk.setCostPerUnit(3.0);
    }

    @Test
    void testLogWasteDeductsIngredientStockAndCalculatesLoss() {
        when(ingredientRepo.findById(5L)).thenReturn(Optional.of(milk));
        when(wasteLogRepo.save(any(WasteLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WasteLogDto dto = new WasteLogDto();
        dto.setIngredientId(5L);
        dto.setIngredientName("Milk");
        dto.setQuantity(2.0); // 2 litres expired
        dto.setReason("EXPIRED");
        dto.setWasteDate(LocalDate.now());

        WasteLog logged = wasteLogService.logWaste(dto);

        assertNotNull(logged);
        assertEquals("EXPIRED", logged.getReason());
        // Loss should be 2.0 * 3.0 = 6.0
        assertEquals(6.0, logged.getEstimatedCostLoss(), 0.001);
        // Milk stock was 10.0 -> now should be 8.0
        assertEquals(8.0, milk.getQuantity(), 0.001);
        verify(ingredientRepo).save(milk);
        verify(wasteLogRepo).save(any(WasteLog.class));
    }
}
