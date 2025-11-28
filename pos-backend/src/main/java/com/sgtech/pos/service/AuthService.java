package com.sgtech.pos.service;

import com.sgtech.pos.dto.LoginRequest;
import com.sgtech.pos.dto.LoginResponse;
import com.sgtech.pos.model.Employee;
import com.sgtech.pos.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    public LoginResponse login(LoginRequest request) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(request.getUsername());
        
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }
        
        Employee employee = employeeOpt.get();
        
        // For legacy migration: if password is not hashed, check plain text
        // In production, all passwords should be hashed
        boolean passwordMatches = false;
        if (employee.getPasswordHash().length() < 60) {
            // Likely plain text password from legacy system
            passwordMatches = employee.getPasswordHash().equals(request.getPassword());
        } else {
            // BCrypt hashed password
            passwordMatches = passwordEncoder.matches(request.getPassword(), employee.getPasswordHash());
        }
        
        if (!passwordMatches) {
            throw new RuntimeException("Invalid username or password");
        }
        
        String token = jwtService.generateToken(employee);
        
        return new LoginResponse(
            token,
            employee.getId(),
            employee.getUsername(),
            employee.getFullName(),
            employee.getPosition()
        );
    }
}

