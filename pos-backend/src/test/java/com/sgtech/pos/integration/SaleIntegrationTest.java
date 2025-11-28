package com.sgtech.pos.integration;

import com.sgtech.pos.dto.SaleRequest;
import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import com.sgtech.pos.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class SaleIntegrationTest {

    @Autowired
    private SaleService saleService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Employee employee;
    private Item item1;
    private Item item2;
    private Coupon coupon;

    @BeforeEach
    public void setUp() {
        // Create employee
        employee = new Employee();
        employee.setUsername("cashier");
        employee.setFirstName("Test");
        employee.setLastName("Cashier");
        employee.setPosition("Cashier");
        employee.setPasswordHash(passwordEncoder.encode("password"));
        employee = employeeRepository.save(employee);

        // Create items
        item1 = new Item();
        item1.setItemId(5001);
        item1.setName("Item 1");
        item1.setPrice(new BigDecimal("10.00"));
        item1.setQuantity(100);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setItemId(5002);
        item2.setName("Item 2");
        item2.setPrice(new BigDecimal("20.00"));
        item2.setQuantity(50);
        item2 = itemRepository.save(item2);

        // Create coupon
        coupon = new Coupon();
        coupon.setCode("TEST10");
        coupon.setDiscountPercent(new BigDecimal("10.00"));
        coupon.setActive(true);
        coupon = couponRepository.save(coupon);
    }

    @Test
    public void testCompleteSaleFlowWithCoupon() {
        SaleRequest request = new SaleRequest();
        List<SaleRequest.SaleItemRequest> items = new ArrayList<>();
        
        SaleRequest.SaleItemRequest itemReq1 = new SaleRequest.SaleItemRequest();
        itemReq1.setItemId(5001);
        itemReq1.setQuantity(2);
        items.add(itemReq1);

        SaleRequest.SaleItemRequest itemReq2 = new SaleRequest.SaleItemRequest();
        itemReq2.setItemId(5002);
        itemReq2.setQuantity(1);
        items.add(itemReq2);

        request.setItems(items);
        request.setCouponCode("TEST10");

        Sale sale = saleService.processSale(employee.getId(), request);

        // Verify sale
        assertNotNull(sale);
        assertEquals(new BigDecimal("40.00"), sale.getTotalAmount()); // 20 + 20
        assertTrue(sale.getTaxAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(sale.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("TEST10", sale.getCouponCode());

        // Verify sale items
        List<SaleItem> saleItems = saleItemRepository.findBySaleId(sale.getId());
        assertEquals(2, saleItems.size());

        // Verify inventory updated
        Item updatedItem1 = itemRepository.findByItemId(5001).orElse(null);
        assertEquals(98, updatedItem1.getQuantity()); // 100 - 2

        Item updatedItem2 = itemRepository.findByItemId(5002).orElse(null);
        assertEquals(49, updatedItem2.getQuantity()); // 50 - 1
    }
}

