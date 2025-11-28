package com.sgtech.pos.controller;

import com.sgtech.pos.util.DataMigrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/migration")
@CrossOrigin(origins = "http://localhost:3000")
public class DataMigrationController {
    
    @Autowired
    private DataMigrationUtil dataMigrationUtil;
    
    @PostMapping("/migrate")
    public ResponseEntity<String> migrateData(@RequestParam String databasePath) {
        try {
            dataMigrationUtil.migrateAllData(databasePath);
            return ResponseEntity.ok("Data migration completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Migration failed: " + e.getMessage());
        }
    }
}

