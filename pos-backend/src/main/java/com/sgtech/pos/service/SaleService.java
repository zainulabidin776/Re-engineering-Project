package com.sgtech.pos.service;

import com.sgtech.pos.dto.SaleRequest;
import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SaleService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private SaleItemRepository saleItemRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.06"); // 6% tax
    private static final BigDecimal COUPON_DISCOUNT = new BigDecimal("0.10"); // 10% discount
    
    @Transactional
    public Sale processSale(UUID employeeId, SaleRequest request) {
        // Get employee
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();
        
        for (SaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            // Get item from database
            Item item = itemRepository.findByItemId(itemRequest.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemRequest.getItemId()));
            
            // Check inventory
            if (item.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient inventory for item: " + item.getName());
            }
            
            // Create sale item
            SaleItem saleItem = new SaleItem();
            saleItem.setItem(item);
            saleItem.setQuantity(itemRequest.getQuantity());
            saleItem.setUnitPrice(item.getPrice());
            saleItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            
            subtotal = subtotal.add(saleItem.getSubtotal());
            saleItems.add(saleItem);
            
            // Update inventory
            item.setQuantity(item.getQuantity() - itemRequest.getQuantity());
            itemRepository.save(item);
        }
        
        // Calculate tax
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithTax = subtotal.add(taxAmount);
        
        // Apply coupon if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCodeAndActiveTrue(request.getCouponCode());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                discountAmount = totalWithTax.multiply(coupon.getDiscountPercent())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                couponCode = coupon.getCode();
            }
        }
        
        BigDecimal finalTotal = totalWithTax.subtract(discountAmount);
        
        // Create sale
        Sale sale = new Sale();
        sale.setEmployee(employee);
        sale.setTotalAmount(subtotal);
        sale.setTaxAmount(taxAmount);
        sale.setDiscountAmount(discountAmount);
        sale.setFinalTotal(finalTotal);
        sale.setCouponCode(couponCode);
        
        sale = saleRepository.save(sale);
        
        // Set sale reference and save sale items
        for (SaleItem saleItem : saleItems) {
            saleItem.setSale(sale);
            saleItemRepository.save(saleItem);
        }
        
        sale.setSaleItems(saleItems);
        return sale;
    }
    
    public List<Sale> getSalesByEmployee(UUID employeeId) {
        return saleRepository.findByEmployeeId(employeeId);
    }
    
    public Optional<Sale> getSaleById(UUID saleId) {
        return saleRepository.findById(saleId);
    }
}

