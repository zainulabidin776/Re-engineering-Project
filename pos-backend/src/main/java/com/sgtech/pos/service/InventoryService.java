package com.sgtech.pos.service;

import com.sgtech.pos.model.Item;
import com.sgtech.pos.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    public Optional<Item> getItemById(UUID id) {
        return itemRepository.findById(id);
    }
    
    public Optional<Item> getItemByItemId(Integer itemId) {
        return itemRepository.findByItemId(itemId);
    }
    
    public List<Item> searchItems(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Item> getLowStockItems(int threshold) {
        return itemRepository.findByQuantityLessThanEqual(threshold);
    }
    
    @Transactional
    public Item updateItemQuantity(UUID itemId, int newQuantity) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
        
        item.setQuantity(newQuantity);
        return itemRepository.save(item);
    }
    
    @Transactional
    public Item updateItemQuantityByItemId(Integer itemId, int newQuantity) {
        Item item = itemRepository.findByItemId(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
        
        item.setQuantity(newQuantity);
        return itemRepository.save(item);
    }
}

