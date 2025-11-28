package com.sgtech.pos.service;

import com.sgtech.pos.model.Employee;
import com.sgtech.pos.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Optional<Employee> getEmployeeById(UUID id) {
        return employeeRepository.findById(id);
    }
    
    public List<Employee> getEmployeesByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }
    
    @Transactional
    public Employee createEmployee(String username, String firstName, String lastName, 
                                   String position, String password) {
        if (employeeRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        String passwordHash = passwordEncoder.encode(password);
        Employee employee = new Employee(username, firstName, lastName, position, passwordHash);
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public Employee updateEmployee(UUID id, String firstName, String lastName, 
                                   String position, String password) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPosition(position);
        
        if (password != null && !password.isEmpty()) {
            employee.setPasswordHash(passwordEncoder.encode(password));
        }
        
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }
}

