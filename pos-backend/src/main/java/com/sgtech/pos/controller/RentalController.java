package com.sgtech.pos.controller;

import com.sgtech.pos.dto.RentalRequest;
import com.sgtech.pos.model.Rental;
import com.sgtech.pos.model.RentalItem;
import com.sgtech.pos.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "http://localhost:3000")
public class RentalController {
    
    @Autowired
    private RentalService rentalService;
    
    @PostMapping
    public ResponseEntity<Rental> createRental(
            @RequestHeader("X-Employee-Id") UUID employeeId,
            @Valid @RequestBody RentalRequest request) {
        try {
            Rental rental = rentalService.processRental(employeeId, request);
            return ResponseEntity.ok(rental);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable UUID id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{phone}")
    public ResponseEntity<List<Rental>> getRentalsByCustomer(@PathVariable String phone) {
        List<Rental> rentals = rentalService.getRentalsByCustomer(phone);
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/outstanding/{phone}")
    public ResponseEntity<List<RentalItem>> getOutstandingRentals(@PathVariable String phone) {
        List<RentalItem> items = rentalService.getOutstandingRentals(phone);
        return ResponseEntity.ok(items);
    }
}

