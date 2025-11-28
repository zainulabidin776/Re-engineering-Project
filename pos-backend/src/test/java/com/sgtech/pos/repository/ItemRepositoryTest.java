package com.sgtech.pos.repository;

import com.sgtech.pos.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private Item testItem;

    @BeforeEach
    public void setUp() {
        testItem = new Item();
        testItem.setItemId(1001);
        testItem.setName("Test Item");
        testItem.setPrice(new BigDecimal("10.00"));
        testItem.setQuantity(100);
        entityManager.persistAndFlush(testItem);
    }

    @Test
    public void testFindByItemId() {
        Optional<Item> found = itemRepository.findByItemId(1001);
        assertTrue(found.isPresent());
        assertEquals("Test Item", found.get().getName());
    }

    @Test
    public void testFindByItemIdNotFound() {
        Optional<Item> found = itemRepository.findByItemId(9999);
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindByQuantityGreaterThan() {
        Item item2 = new Item();
        item2.setItemId(1002);
        item2.setName("Low Stock Item");
        item2.setPrice(new BigDecimal("5.00"));
        item2.setQuantity(5);
        entityManager.persistAndFlush(item2);

        List<Item> lowStock = itemRepository.findByQuantityGreaterThan(10);
        assertEquals(1, lowStock.size());
        assertEquals(1001, lowStock.get(0).getItemId());
    }

    @Test
    public void testFindByNameContainingIgnoreCase() {
        List<Item> found = itemRepository.findByNameContainingIgnoreCase("test");
        assertFalse(found.isEmpty());
        assertEquals("Test Item", found.get(0).getName());
    }
}

