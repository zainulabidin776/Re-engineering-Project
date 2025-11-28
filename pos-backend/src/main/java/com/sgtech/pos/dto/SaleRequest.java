package com.sgtech.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class SaleRequest {
    
    @NotEmpty(message = "Sale items are required")
    @Valid
    private List<SaleItemRequest> items;
    
    private String couponCode;
    
    public SaleRequest() {}
    
    public List<SaleItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }
    
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public static class SaleItemRequest {
        private Integer itemId;
        private Integer quantity;
        
        public SaleItemRequest() {}
        
        public SaleItemRequest(Integer itemId, Integer quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }
        
        public Integer getItemId() {
            return itemId;
        }
        
        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}

