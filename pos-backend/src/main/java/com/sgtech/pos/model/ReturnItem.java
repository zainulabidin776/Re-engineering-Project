package com.sgtech.pos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "return_items")
public class ReturnItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "return_id", nullable = false)
    private Return returnEntity;
    
    @ManyToOne
    @JoinColumn(name = "rental_item_id", nullable = false)
    private RentalItem rentalItem;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public ReturnItem() {}
    
    public ReturnItem(Return returnEntity, RentalItem rentalItem, Integer quantity, BigDecimal refundAmount) {
        this.returnEntity = returnEntity;
        this.rentalItem = rentalItem;
        this.quantity = quantity;
        this.refundAmount = refundAmount;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Return getReturnEntity() {
        return returnEntity;
    }
    
    public void setReturnEntity(Return returnEntity) {
        this.returnEntity = returnEntity;
    }
    
    public RentalItem getRentalItem() {
        return rentalItem;
    }
    
    public void setRentalItem(RentalItem rentalItem) {
        this.rentalItem = rentalItem;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

