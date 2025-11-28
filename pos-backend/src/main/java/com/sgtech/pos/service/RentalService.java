package com.sgtech.pos.service;

import com.sgtech.pos.dto.RentalRequest;
import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentalService {
    
    @Autowired
    private RentalRepository rentalRepository;
    
    @Autowired
    private RentalItemRepository rentalItemRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.06"); // 6% tax
    
    @Transactional
    public Rental processRental(UUID employeeId, RentalRequest request) {
        // Get or create customer
        Customer customer = customerRepository.findByPhone(request.getCustomerPhone())
            .orElseGet(() -> {
                Customer newCustomer = new Customer(request.getCustomerPhone());
                return customerRepository.save(newCustomer);
            });
        
        // Get employee
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        List<RentalItem> rentalItems = new ArrayList<>();
        
        for (RentalRequest.RentalItemRequest itemRequest : request.getItems()) {
            // Get item from database
            Item item = itemRepository.findByItemId(itemRequest.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemRequest.getItemId()));
            
            // Check inventory
            if (item.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient inventory for item: " + item.getName());
            }
            
            // Create rental item
            RentalItem rentalItem = new RentalItem();
            rentalItem.setItem(item);
            rentalItem.setQuantity(itemRequest.getQuantity());
            rentalItem.setUnitPrice(item.getPrice());
            
            BigDecimal itemSubtotal = item.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
            rentalItems.add(rentalItem);
            
            // Update inventory
            item.setQuantity(item.getQuantity() - itemRequest.getQuantity());
            itemRepository.save(item);
        }
        
        // Calculate tax
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithTax = subtotal.add(taxAmount);
        
        // Create rental
        Rental rental = new Rental();
        rental.setCustomer(customer);
        rental.setEmployee(employee);
        rental.setDueDate(request.getDueDate());
        rental.setTotalAmount(subtotal);
        rental.setTaxAmount(taxAmount);
        
        rental = rentalRepository.save(rental);
        
        // Set rental reference and save rental items
        for (RentalItem rentalItem : rentalItems) {
            rentalItem.setRental(rental);
            rentalItemRepository.save(rentalItem);
        }
        
        rental.setRentalItems(rentalItems);
        return rental;
    }
    
    public List<Rental> getRentalsByCustomer(String phone) {
        Optional<Customer> customerOpt = customerRepository.findByPhone(phone);
        if (customerOpt.isEmpty()) {
            return new ArrayList<>();
        }
        return rentalRepository.findByCustomerId(customerOpt.get().getId());
    }
    
    public List<RentalItem> getOutstandingRentals(String phone) {
        Optional<Customer> customerOpt = customerRepository.findByPhone(phone);
        if (customerOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Rental> rentals = rentalRepository.findByCustomerId(customerOpt.get().getId());
        List<RentalItem> outstandingItems = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (Rental rental : rentals) {
            for (RentalItem item : rental.getRentalItems()) {
                if (!item.getReturned()) {
                    // Calculate days overdue
                    long daysOverdue = ChronoUnit.DAYS.between(rental.getDueDate(), today);
                    if (daysOverdue > 0) {
                        item.setDaysOverdue((int) daysOverdue);
                    }
                    outstandingItems.add(item);
                }
            }
        }
        
        return outstandingItems;
    }
    
    public Optional<Rental> getRentalById(UUID rentalId) {
        return rentalRepository.findById(rentalId);
    }
}

