package com.sgtech.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class ReturnRequest {
    
    @NotEmpty(message = "Return items are required")
    @Valid
    private List<ReturnItemRequest> items;
    
    public ReturnRequest() {}
    
    public List<ReturnItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<ReturnItemRequest> items) {
        this.items = items;
    }
    
    public static class ReturnItemRequest {
        private UUID rentalItemId;
        private Integer quantity;
        
        public ReturnItemRequest() {}
        
        public ReturnItemRequest(UUID rentalItemId, Integer quantity) {
            this.rentalItemId = rentalItemId;
            this.quantity = quantity;
        }
        
        public UUID getRentalItemId() {
            return rentalItemId;
        }
        
        public void setRentalItemId(UUID rentalItemId) {
            this.rentalItemId = rentalItemId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}

