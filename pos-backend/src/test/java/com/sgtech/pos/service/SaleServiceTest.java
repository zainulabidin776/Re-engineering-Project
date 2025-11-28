package com.sgtech.pos.service;

import com.sgtech.pos.dto.SaleRequest;
import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SaleServiceTest {

    @Autowired
    private SaleService saleService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private Employee employee;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        // Create test employee
        employee = new Employee();
        employee.setUsername("cashier1");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Cashier");
        employee.setPasswordHash(passwordEncoder.encode("password"));
        employee = employeeRepository.save(employee);

        // Create test items
        item1 = new Item();
        item1.setItemId(1001);
        item1.setName("Test Item 1");
        item1.setPrice(new BigDecimal("10.00"));
        item1.setQuantity(100);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setItemId(1002);
        item2.setName("Test Item 2");
        item2.setPrice(new BigDecimal("20.00"));
        item2.setQuantity(50);
        item2 = itemRepository.save(item2);
    }

    @Test
    public void testProcessSaleSuccess() {
        SaleRequest request = new SaleRequest();
        List<SaleRequest.SaleItemRequest> items = new ArrayList<>();
        
        SaleRequest.SaleItemRequest itemReq1 = new SaleRequest.SaleItemRequest();
        itemReq1.setItemId(1001);
        itemReq1.setQuantity(2);
        items.add(itemReq1);

        request.setItems(items);

        Sale sale = saleService.processSale(employee.getId(), request);

        assertNotNull(sale);
        assertNotNull(sale.getId());
        assertEquals(employee.getId(), sale.getEmployee().getId());
        assertEquals(new BigDecimal("20.00"), sale.getTotalAmount());
        assertTrue(sale.getTaxAmount().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(sale.getSaleItems());
        assertEquals(1, sale.getSaleItems().size());

        // Verify inventory updated
        Item updatedItem = itemRepository.findByItemId(1001).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(98, updatedItem.getQuantity()); // 100 - 2
    }

    @Test
    public void testProcessSaleWithMultipleItems() {
        SaleRequest request = new SaleRequest();
        List<SaleRequest.SaleItemRequest> items = new ArrayList<>();
        
        SaleRequest.SaleItemRequest itemReq1 = new SaleRequest.SaleItemRequest();
        itemReq1.setItemId(1001);
        itemReq1.setQuantity(2);
        items.add(itemReq1);

        SaleRequest.SaleItemRequest itemReq2 = new SaleRequest.SaleItemRequest();
        itemReq2.setItemId(1002);
        itemReq2.setQuantity(1);
        items.add(itemReq2);

        request.setItems(items);

        Sale sale = saleService.processSale(employee.getId(), request);

        assertNotNull(sale);
        assertEquals(new BigDecimal("40.00"), sale.getTotalAmount()); // 20 + 20
        assertEquals(2, sale.getSaleItems().size());
    }

    @Test
    public void testProcessSaleInsufficientInventory() {
        SaleRequest request = new SaleRequest();
        List<SaleRequest.SaleItemRequest> items = new ArrayList<>();
        
        SaleRequest.SaleItemRequest itemReq = new SaleRequest.SaleItemRequest();
        itemReq.setItemId(1001);
        itemReq.setQuantity(200); // More than available
        items.add(itemReq);

        request.setItems(items);

        assertThrows(RuntimeException.class, () -> {
            saleService.processSale(employee.getId(), request);
        });
    }
}

