package com.sgtech.pos.controller;

import com.sgtech.pos.dto.SaleRequest;
import com.sgtech.pos.model.Sale;
import com.sgtech.pos.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:3000")
public class SaleController {
    
    @Autowired
    private SaleService saleService;
    
    @PostMapping
    public ResponseEntity<Sale> createSale(
            @RequestHeader("X-Employee-Id") UUID employeeId,
            @Valid @RequestBody SaleRequest request) {
        try {
            Sale sale = saleService.processSale(employeeId, request);
            return ResponseEntity.ok(sale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable UUID id) {
        return saleService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Sale>> getSalesByEmployee(@PathVariable UUID employeeId) {
        List<Sale> sales = saleService.getSalesByEmployee(employeeId);
        return ResponseEntity.ok(sales);
    }
}

