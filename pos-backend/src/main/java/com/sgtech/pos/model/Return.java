package com.sgtech.pos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "returns")
public class Return {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    
    @Column(name = "total_refund", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRefund;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItem> returnItems;
    
    @PrePersist
    protected void onCreate() {
        returnDate = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Return() {}
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Rental getRental() {
        return rental;
    }
    
    public void setRental(Rental rental) {
        this.rental = rental;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public LocalDateTime getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
    
    public BigDecimal getTotalRefund() {
        return totalRefund;
    }
    
    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<ReturnItem> getReturnItems() {
        return returnItems;
    }
    
    public void setReturnItems(List<ReturnItem> returnItems) {
        this.returnItems = returnItems;
    }
}

