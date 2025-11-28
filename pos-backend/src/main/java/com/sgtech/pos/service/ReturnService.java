package com.sgtech.pos.service;

import com.sgtech.pos.dto.ReturnRequest;
import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReturnService {
    
    @Autowired
    private ReturnRepository returnRepository;
    
    @Autowired
    private ReturnItemRepository returnItemRepository;
    
    @Autowired
    private RentalItemRepository rentalItemRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Transactional
    public Return processReturn(UUID employeeId, ReturnRequest request) {
        // Get employee
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Process return items
        BigDecimal totalRefund = BigDecimal.ZERO;
        List<ReturnItem> returnItems = new ArrayList<>();
        Rental rental = null;
        
        for (ReturnRequest.ReturnItemRequest itemRequest : request.getItems()) {
            // Get rental item
            RentalItem rentalItem = rentalItemRepository.findById(itemRequest.getRentalItemId())
                .orElseThrow(() -> new RuntimeException("Rental item not found"));
            
            // Validate quantity
            if (itemRequest.getQuantity() > rentalItem.getQuantity() || itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("Invalid return quantity");
            }
            
            if (rentalItem.getReturned()) {
                throw new RuntimeException("Item already returned");
            }
            
            // Get rental for return entity
            if (rental == null) {
                rental = rentalItem.getRental();
            }
            
            // Calculate refund (original price)
            BigDecimal refundAmount = rentalItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalRefund = totalRefund.add(refundAmount);
            
            // Create return item
            ReturnItem returnItem = new ReturnItem();
            returnItem.setRentalItem(rentalItem);
            returnItem.setQuantity(itemRequest.getQuantity());
            returnItem.setRefundAmount(refundAmount);
            returnItems.add(returnItem);
            
            // Update rental item
            if (itemRequest.getQuantity().equals(rentalItem.getQuantity())) {
                rentalItem.setReturned(true);
                rentalItem.setReturnDate(LocalDate.now());
            } else {
                // Partial return - create new rental item for remaining quantity
                RentalItem remainingItem = new RentalItem();
                remainingItem.setRental(rental);
                remainingItem.setItem(rentalItem.getItem());
                remainingItem.setQuantity(rentalItem.getQuantity() - itemRequest.getQuantity());
                remainingItem.setUnitPrice(rentalItem.getUnitPrice());
                rentalItemRepository.save(remainingItem);
                
                rentalItem.setQuantity(itemRequest.getQuantity());
                rentalItem.setReturned(true);
                rentalItem.setReturnDate(LocalDate.now());
            }
            
            rentalItemRepository.save(rentalItem);
            
            // Restore inventory
            Item item = rentalItem.getItem();
            item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
            itemRepository.save(item);
        }
        
        if (rental == null) {
            throw new RuntimeException("No rental found for return items");
        }
        
        // Create return
        Return returnEntity = new Return();
        returnEntity.setRental(rental);
        returnEntity.setEmployee(employee);
        returnEntity.setTotalRefund(totalRefund);
        
        returnEntity = returnRepository.save(returnEntity);
        
        // Set return reference and save return items
        for (ReturnItem returnItem : returnItems) {
            returnItem.setReturnEntity(returnEntity);
            returnItemRepository.save(returnItem);
        }
        
        returnEntity.setReturnItems(returnItems);
        return returnEntity;
    }
    
    public Optional<Return> getReturnById(UUID returnId) {
        return returnRepository.findById(returnId);
    }
}

