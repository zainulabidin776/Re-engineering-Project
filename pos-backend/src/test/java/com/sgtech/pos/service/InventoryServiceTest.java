package com.sgtech.pos.service;

import com.sgtech.pos.model.Item;
import com.sgtech.pos.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ItemRepository itemRepository;

    private Item testItem;

    @BeforeEach
    public void setUp() {
        testItem = new Item();
        testItem.setItemId(4001);
        testItem.setName("Test Inventory Item");
        testItem.setPrice(new BigDecimal("15.00"));
        testItem.setQuantity(50);
        testItem = itemRepository.save(testItem);
    }

    @Test
    public void testGetAllItems() {
        List<Item> items = inventoryService.getAllItems();
        assertFalse(items.isEmpty());
        assertTrue(items.stream().anyMatch(i -> i.getItemId().equals(4001)));
    }

    @Test
    public void testGetItemByItemId() {
        Optional<Item> found = inventoryService.getItemByItemId(4001);
        assertTrue(found.isPresent());
        assertEquals("Test Inventory Item", found.get().getName());
    }

    @Test
    public void testSearchItems() {
        List<Item> results = inventoryService.searchItems("Inventory");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(i -> i.getName().contains("Inventory")));
    }

    @Test
    public void testGetLowStockItems() {
        Item lowStockItem = new Item();
        lowStockItem.setItemId(4002);
        lowStockItem.setName("Low Stock Item");
        lowStockItem.setPrice(new BigDecimal("10.00"));
        lowStockItem.setQuantity(5);
        itemRepository.save(lowStockItem);

        List<Item> lowStock = inventoryService.getLowStockItems(10);
        assertTrue(lowStock.stream().anyMatch(i -> i.getItemId().equals(4002)));
    }

    @Test
    public void testUpdateItemQuantity() {
        UUID itemId = testItem.getId();
        Item updated = inventoryService.updateItemQuantity(itemId, 75);

        assertEquals(75, updated.getQuantity());
    }

    @Test
    public void testUpdateItemQuantityNegative() {
        UUID itemId = testItem.getId();
        assertThrows(RuntimeException.class, () -> {
            inventoryService.updateItemQuantity(itemId, -1);
        });
    }

    @Test
    public void testUpdateItemQuantityByItemId() {
        Item updated = inventoryService.updateItemQuantityByItemId(4001, 100);
        assertEquals(100, updated.getQuantity());
    }
}

