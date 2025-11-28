package com.sgtech.pos.service;

import com.sgtech.pos.dto.ReturnRequest;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReturnServiceTest {

    @Autowired
    private ReturnService returnService;

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
    private RentalItemRepository rentalItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Employee employee;
    private Customer customer;
    private Item item;
    private Rental rental;
    private RentalItem rentalItem;

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

        // Create test customer
        customer = new Customer();
        customer.setPhone("1234567890");
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer = customerRepository.save(customer);

        // Create test item
        item = new Item();
        item.setItemId(3001);
        item.setName("Rental Item");
        item.setPrice(new BigDecimal("25.00"));
        item.setQuantity(100);
        item = itemRepository.save(item);

        // Create rental
        rental = new Rental();
        rental.setCustomer(customer);
        rental.setEmployee(employee);
        rental.setDueDate(LocalDate.now().plusDays(7));
        rental.setTotalAmount(new BigDecimal("25.00"));
        rental.setTaxAmount(new BigDecimal("1.50"));
        rental = rentalRepository.save(rental);

        // Create rental item
        rentalItem = new RentalItem();
        rentalItem.setRental(rental);
        rentalItem.setItem(item);
        rentalItem.setQuantity(1);
        rentalItem.setUnitPrice(new BigDecimal("25.00"));
        rentalItem.setReturned(false);
        rentalItem = rentalItemRepository.save(rentalItem);
    }

    @Test
    public void testProcessReturnSuccess() {
        // Update item quantity (simulate it was rented)
        item.setQuantity(99);
        itemRepository.save(item);

        ReturnRequest request = new ReturnRequest();
        List<ReturnRequest.ReturnItemRequest> items = new ArrayList<>();
        
        ReturnRequest.ReturnItemRequest itemReq = new ReturnRequest.ReturnItemRequest();
        itemReq.setRentalItemId(rentalItem.getId());
        itemReq.setQuantity(1);
        items.add(itemReq);

        request.setItems(items);

        Return returnEntity = returnService.processReturn(employee.getId(), request);

        assertNotNull(returnEntity);
        assertNotNull(returnEntity.getId());
        assertEquals(employee.getId(), returnEntity.getEmployee().getId());
        assertTrue(returnEntity.getTotalRefund().compareTo(BigDecimal.ZERO) > 0);

        // Verify inventory restored
        Item updatedItem = itemRepository.findByItemId(3001).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(100, updatedItem.getQuantity()); // Restored to original

        // Verify rental item marked as returned
        RentalItem updatedRentalItem = rentalItemRepository.findById(rentalItem.getId()).orElse(null);
        assertNotNull(updatedRentalItem);
        assertTrue(updatedRentalItem.getReturned());
    }

    @Test
    public void testProcessReturnInvalidQuantity() {
        ReturnRequest request = new ReturnRequest();
        List<ReturnRequest.ReturnItemRequest> items = new ArrayList<>();
        
        ReturnRequest.ReturnItemRequest itemReq = new ReturnRequest.ReturnItemRequest();
        itemReq.setRentalItemId(rentalItem.getId());
        itemReq.setQuantity(2); // More than available
        items.add(itemReq);

        request.setItems(items);

        assertThrows(RuntimeException.class, () -> {
            returnService.processReturn(employee.getId(), request);
        });
    }
}

