package com.sgtech.pos.util;

import com.sgtech.pos.model.*;
import com.sgtech.pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for migrating data from legacy .txt files to PostgreSQL database
 */
@Component
public class DataMigrationUtil {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = 
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    
    @Transactional
    public void migrateEmployees(String filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\s+");
                if (parts.length >= 5) {
                    String username = parts[0];
                    String position = parts[1];
                    String firstName = parts[2];
                    String lastName = parts[3];
                    String password = parts[4];
                    
                    // Hash password
                    String passwordHash = passwordEncoder.encode(password);
                    
                    Employee employee = new Employee(username, firstName, lastName, position, passwordHash);
                    employeeRepository.save(employee);
                }
            }
        }
    }
    
    @Transactional
    public void migrateItems(String filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    Integer itemId = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    BigDecimal price = new BigDecimal(parts[2]);
                    Integer quantity = Integer.parseInt(parts[3]);
                    
                    Item item = new Item(itemId, name, price, quantity);
                    itemRepository.save(item);
                }
            }
        }
    }
    
    @Transactional
    public void migrateCoupons(String filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String code = line.trim();
                // Default 10% discount for legacy coupons
                BigDecimal discountPercent = new BigDecimal("10.00");
                
                Coupon coupon = new Coupon(code, discountPercent);
                couponRepository.save(coupon);
            }
        }
    }
    
    public void migrateAllData(String databasePath) throws Exception {
        System.out.println("Starting data migration...");
        
        System.out.println("Migrating employees...");
        migrateEmployees(databasePath + "/employeeDatabase.txt");
        
        System.out.println("Migrating items...");
        migrateItems(databasePath + "/itemDatabase.txt");
        
        System.out.println("Migrating coupons...");
        migrateCoupons(databasePath + "/couponNumber.txt");
        
        System.out.println("Data migration completed!");
    }
}

