package org.example.bakemaster_backend;

import org.example.bakemaster_backend.dto.OrderCreateDto;
import org.example.bakemaster_backend.dto.OrderItemDto;
import org.example.bakemaster_backend.dto.UpdateStatusDto;
import org.example.bakemaster_backend.entity.*;
import org.example.bakemaster_backend.repository.*;
import org.example.bakemaster_backend.service.OrderService;
import org.example.bakemaster_backend.service.ProductionTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private IngredientRepository ingredientRepo;

    @Mock
    private ProductionTaskService productionTaskService;

    @Mock
    private ProductionTaskRepository productionTaskRepo;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product cakeProduct;
    private Ingredient flour;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "Alice Johnson", "111-222", "alice@example.com", 100);

        flour = new Ingredient();
        flour.setId(101L);
        flour.setName("Flour");
        flour.setQuantity(20.0);
        flour.setUnit("kg");
        flour.setCostPerUnit(2.5);

        cakeProduct = new Product();
        cakeProduct.setId(50L);
        cakeProduct.setName("Chocolate Cake");
        cakeProduct.setSellingPrice(40.0);

        RecipeItem ri = new RecipeItem(1L, cakeProduct, flour, 0.5, "kg");
        cakeProduct.setRecipeItems(List.of(ri));
    }

    @Test
    void testCreateOrderCalculatesTotalAndSetsReceived() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderCreateDto dto = new OrderCreateDto();
        dto.setCustomerId(1L);
        dto.setChannel("WALK_IN");
        dto.setDeliveryDeadline(LocalDate.now().plusDays(1));

        OrderItemDto itemDto = new OrderItemDto(50L, "Chocolate Cake", 2, 40.0, "Extra chocolate");
        dto.setItems(List.of(itemDto));

        OrderEntity created = orderService.createOrder(dto);

        assertNotNull(created);
        assertEquals("RECEIVED", created.getStatus());
        assertEquals(80.0, created.getTotalAmount());
        assertEquals(1, created.getItems().size());
        assertEquals(80.0, created.getItems().get(0).getSubtotal());
    }

    @Test
    void testConfirmOrderDeductsIngredientsAndGeneratesTasks() {
        OrderEntity order = new OrderEntity();
        order.setId(10L);
        order.setCustomerId(1L);
        order.setStatus("RECEIVED");

        OrderItem item = new OrderItem(1L, 50L, "Chocolate Cake", 2, 40.0, 80.0, null, order);
        order.setItems(new ArrayList<>(List.of(item)));

        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(productRepo.findById(50L)).thenReturn(Optional.of(cakeProduct));
        when(ingredientRepo.findById(101L)).thenReturn(Optional.of(flour));
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity confirmed = orderService.confirmOrder(10L);

        assertEquals("CONFIRMED", confirmed.getStatus());
        // Initial flour was 20kg. 2 cakes * 0.5kg = 1kg required -> remaining should be 19kg
        assertEquals(19.0, flour.getQuantity(), 0.001);
        verify(ingredientRepo).save(flour);
        verify(productionTaskService).generateTasksForOrder(order);
    }

    @Test
    void testConfirmOrderFailsWhenStockInsufficient() {
        flour.setQuantity(0.2); // Not enough for 2 cakes (needs 1.0kg)

        OrderEntity order = new OrderEntity();
        order.setId(10L);
        order.setStatus("RECEIVED");
        OrderItem item = new OrderItem(1L, 50L, "Chocolate Cake", 2, 40.0, 80.0, null, order);
        order.setItems(new ArrayList<>(List.of(item)));

        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(productRepo.findById(50L)).thenReturn(Optional.of(cakeProduct));
        when(ingredientRepo.findById(101L)).thenReturn(Optional.of(flour));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.confirmOrder(10L));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testCancelOrderRestoresStock() {
        flour.setQuantity(19.0);

        OrderEntity order = new OrderEntity();
        order.setId(10L);
        order.setStatus("CONFIRMED");
        OrderItem item = new OrderItem(1L, 50L, "Chocolate Cake", 2, 40.0, 80.0, null, order);
        order.setItems(new ArrayList<>(List.of(item)));

        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(productRepo.findById(50L)).thenReturn(Optional.of(cakeProduct));
        when(ingredientRepo.findById(101L)).thenReturn(Optional.of(flour));
        when(productionTaskRepo.findByOrderId(10L)).thenReturn(new ArrayList<>());
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity cancelled = orderService.cancelOrder(10L);

        assertEquals("CANCELLED", cancelled.getStatus());
        // Restored 1kg flour -> 19 + 1 = 20kg
        assertEquals(20.0, flour.getQuantity(), 0.001);
    }

    @Test
    void testInvalidStatusTransitionRejected() {
        OrderEntity order = new OrderEntity();
        order.setId(10L);
        order.setStatus("RECEIVED");

        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));

        UpdateStatusDto dto = new UpdateStatusDto();
        dto.setStatus("DELIVERED"); // Cannot jump directly from RECEIVED to DELIVERED

        assertThrows(RuntimeException.class, () -> orderService.updateStatus(10L, dto));
    }
}
