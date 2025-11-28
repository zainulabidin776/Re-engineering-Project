package com.sgtech.pos.controller;

import com.sgtech.pos.dto.ReturnRequest;
import com.sgtech.pos.model.Return;
import com.sgtech.pos.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/returns")
@CrossOrigin(origins = "http://localhost:3000")
public class ReturnController {
    
    @Autowired
    private ReturnService returnService;
    
    @PostMapping
    public ResponseEntity<Return> createReturn(
            @RequestHeader("X-Employee-Id") UUID employeeId,
            @Valid @RequestBody ReturnRequest request) {
        try {
            Return returnEntity = returnService.processReturn(employeeId, request);
            return ResponseEntity.ok(returnEntity);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Return> getReturnById(@PathVariable UUID id) {
        return returnService.getReturnById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

