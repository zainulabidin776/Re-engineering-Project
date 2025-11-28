import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Characterization tests for Inventory class (Singleton pattern)
 * These tests lock down current behavior before refactoring
 */
public class InventoryTest {
    
    private Inventory inventory;
    private List<Item> databaseItems;
    private List<Item> transactionItems;
    
    @Before
    public void setUp() {
        // Get singleton instance
        inventory = Inventory.getInstance();
        databaseItems = new ArrayList<Item>();
        transactionItems = new ArrayList<Item>();
    }
    
    @Test
    public void testSingletonPattern() {
        Inventory instance1 = Inventory.getInstance();
        Inventory instance2 = Inventory.getInstance();
        assertSame("Singleton should return same instance", instance1, instance2);
    }
    
    @Test
    public void testAccessInventoryWithValidFile() {
        // This test requires Database/itemDatabase.txt to exist
        String databaseFile = "Database/itemDatabase.txt";
        boolean result = inventory.accessInventory(databaseFile, databaseItems);
        
        // Should successfully read file if it exists
        assertTrue("Should be able to access inventory file", result);
        assertTrue("Database items list should be populated", databaseItems.size() > 0);
    }
    
    @Test
    public void testAccessInventoryWithInvalidFile() {
        String invalidFile = "Database/nonexistent.txt";
        boolean result = inventory.accessInventory(invalidFile, databaseItems);
        
        assertFalse("Should return false for non-existent file", result);
    }
    
    @Test
    public void testUpdateInventoryTakeFromInventory() {
        // Setup: Add item to database
        databaseItems.add(new Item(1000, "TestItem", 10.0f, 100));
        
        // Setup: Add item to transaction (taking 5 items)
        transactionItems.add(new Item(1000, "TestItem", 10.0f, 5));
        
        // Execute: Update inventory (take from inventory)
        inventory.updateInventory("Database/test.txt", transactionItems, databaseItems, true);
        
        // Verify: Inventory should decrease
        Item updatedItem = databaseItems.get(0);
        assertEquals("Inventory should decrease by transaction amount", 95, updatedItem.getAmount());
    }
    
    @Test
    public void testUpdateInventoryReturnToInventory() {
        // Setup: Add item to database
        databaseItems.add(new Item(1000, "TestItem", 10.0f, 100));
        
        // Setup: Add item to transaction (returning 5 items)
        transactionItems.add(new Item(1000, "TestItem", 10.0f, 5));
        
        // Execute: Update inventory (return to inventory)
        inventory.updateInventory("Database/test.txt", transactionItems, databaseItems, false);
        
        // Verify: Inventory should increase
        Item updatedItem = databaseItems.get(0);
        assertEquals("Inventory should increase by transaction amount", 105, updatedItem.getAmount());
    }
    
    @Test
    public void testUpdateInventoryItemNotFound() {
        // Setup: Add item to database
        databaseItems.add(new Item(1000, "TestItem", 10.0f, 100));
        
        // Setup: Add different item to transaction
        transactionItems.add(new Item(9999, "OtherItem", 5.0f, 5));
        
        // Execute: Update inventory
        inventory.updateInventory("Database/test.txt", transactionItems, databaseItems, true);
        
        // Verify: Original item amount unchanged
        Item originalItem = databaseItems.get(0);
        assertEquals("Item not in transaction should remain unchanged", 100, originalItem.getAmount());
    }
}

