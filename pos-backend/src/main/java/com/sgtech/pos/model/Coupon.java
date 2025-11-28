package com.sgtech.pos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    
    @Column(name = "valid_to")
    private LocalDateTime validTo;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Coupon() {}
    
    public Coupon(String code, BigDecimal discountPercent) {
        this.code = code;
        this.discountPercent = discountPercent;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }
    
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }
    
    public LocalDateTime getValidTo() {
        return validTo;
    }
    
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

