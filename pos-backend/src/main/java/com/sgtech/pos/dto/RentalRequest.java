package com.sgtech.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class RentalRequest {
    
    @NotNull(message = "Customer phone is required")
    private String customerPhone;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotEmpty(message = "Rental items are required")
    @Valid
    private List<RentalItemRequest> items;
    
    public RentalRequest() {}
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public List<RentalItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<RentalItemRequest> items) {
        this.items = items;
    }
    
    public static class RentalItemRequest {
        private Integer itemId;
        private Integer quantity;
        
        public RentalItemRequest() {}
        
        public RentalItemRequest(Integer itemId, Integer quantity) {
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

