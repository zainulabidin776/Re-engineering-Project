package com.sgtech.pos.controller;

import com.sgtech.pos.model.Employee;
import com.sgtech.pos.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Employee>> getEmployeesByPosition(@PathVariable String position) {
        List<Employee> employees = employeeService.getEmployeesByPosition(position);
        return ResponseEntity.ok(employees);
    }
    
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeCreateRequest request) {
        try {
            Employee employee = employeeService.createEmployee(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getPosition(),
                request.getPassword()
            );
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable UUID id,
            @RequestBody EmployeeUpdateRequest request) {
        try {
            Employee employee = employeeService.updateEmployee(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getPosition(),
                request.getPassword()
            );
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTOs for request bodies
    public static class EmployeeCreateRequest {
        private String username;
        private String firstName;
        private String lastName;
        private String position;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class EmployeeUpdateRequest {
        private String firstName;
        private String lastName;
        private String position;
        private String password;
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}

