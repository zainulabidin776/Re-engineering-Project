package com.sgtech.pos.controller;

import com.sgtech.pos.model.Item;
import com.sgtech.pos.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable UUID id) {
        return inventoryService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/items/item-id/{itemId}")
    public ResponseEntity<Item> getItemByItemId(@PathVariable Integer itemId) {
        return inventoryService.getItemByItemId(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/items/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String name) {
        List<Item> items = inventoryService.searchItems(name);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/low-stock")
    public ResponseEntity<List<Item>> getLowStockItems(@RequestParam(defaultValue = "10") int threshold) {
        List<Item> items = inventoryService.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }
    
    @PutMapping("/items/{id}/quantity")
    public ResponseEntity<Item> updateQuantity(
            @PathVariable UUID id,
            @RequestParam int quantity) {
        try {
            Item item = inventoryService.updateItemQuantity(id, quantity);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

