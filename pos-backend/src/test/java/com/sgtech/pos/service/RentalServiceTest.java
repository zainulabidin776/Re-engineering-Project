package com.sgtech.pos.service;

import com.sgtech.pos.dto.RentalRequest;
import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RentalServiceTest {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Employee employee;
    private Item item;
    private String customerPhone = "1234567890";

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

        // Create test item
        item = new Item();
        item.setItemId(2001);
        item.setName("Rental Item");
        item.setPrice(new BigDecimal("15.00"));
        item.setQuantity(50);
        item = itemRepository.save(item);
    }

    @Test
    public void testProcessRentalSuccess() {
        RentalRequest request = new RentalRequest();
        request.setCustomerPhone(customerPhone);
        request.setDueDate(LocalDate.now().plusDays(7));

        List<RentalRequest.RentalItemRequest> items = new ArrayList<>();
        RentalRequest.RentalItemRequest itemReq = new RentalRequest.RentalItemRequest();
        itemReq.setItemId(2001);
        itemReq.setQuantity(2);
        items.add(itemReq);
        request.setItems(items);

        Rental rental = rentalService.processRental(employee.getId(), request);

        assertNotNull(rental);
        assertNotNull(rental.getId());
        assertEquals(customerPhone, rental.getCustomer().getPhone());
        assertEquals(employee.getId(), rental.getEmployee().getId());
        assertNotNull(rental.getRentalItems());
        assertEquals(1, rental.getRentalItems().size());

        // Verify customer was created
        assertTrue(customerRepository.findByPhone(customerPhone).isPresent());

        // Verify inventory updated
        Item updatedItem = itemRepository.findByItemId(2001).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(48, updatedItem.getQuantity()); // 50 - 2
    }

    @Test
    public void testGetOutstandingRentals() {
        // Create a rental first
        RentalRequest request = new RentalRequest();
        request.setCustomerPhone(customerPhone);
        request.setDueDate(LocalDate.now().plusDays(7));

        List<RentalRequest.RentalItemRequest> items = new ArrayList<>();
        RentalRequest.RentalItemRequest itemReq = new RentalRequest.RentalItemRequest();
        itemReq.setItemId(2001);
        itemReq.setQuantity(1);
        items.add(itemReq);
        request.setItems(items);

        rentalService.processRental(employee.getId(), request);

        // Get outstanding rentals
        List<RentalItem> outstanding = rentalService.getOutstandingRentals(customerPhone);
        assertFalse(outstanding.isEmpty());
        assertEquals(1, outstanding.size());
    }
}

