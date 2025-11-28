package com.sgtech.pos.service;

import com.sgtech.pos.model.Employee;
import com.sgtech.pos.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        // Clean up any existing test data
        employeeRepository.deleteAll();
    }

    @Test
    public void testCreateEmployee() {
        Employee employee = employeeService.createEmployee(
            "newuser",
            "New",
            "User",
            "Cashier",
            "password123"
        );

        assertNotNull(employee);
        assertNotNull(employee.getId());
        assertEquals("newuser", employee.getUsername());
        assertEquals("New", employee.getFirstName());
        assertEquals("Cashier", employee.getPosition());
        assertNotEquals("password123", employee.getPasswordHash()); // Should be hashed
    }

    @Test
    public void testCreateEmployeeDuplicateUsername() {
        employeeService.createEmployee("testuser", "Test", "User", "Cashier", "password");

        assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee("testuser", "Another", "User", "Cashier", "password");
        });
    }

    @Test
    public void testUpdateEmployee() {
        Employee employee = employeeService.createEmployee(
            "updateuser",
            "Update",
            "User",
            "Cashier",
            "password123"
        );

        UUID id = employee.getId();
        Employee updated = employeeService.updateEmployee(
            id,
            "Updated",
            "Name",
            "Admin",
            "newpassword"
        );

        assertEquals("Updated", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("Admin", updated.getPosition());
    }

    @Test
    public void testDeleteEmployee() {
        Employee employee = employeeService.createEmployee(
            "deleteuser",
            "Delete",
            "User",
            "Cashier",
            "password123"
        );

        UUID id = employee.getId();
        employeeService.deleteEmployee(id);

        Optional<Employee> deleted = employeeRepository.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testGetEmployeesByPosition() {
        employeeService.createEmployee("cashier1", "Cashier", "One", "Cashier", "pass");
        employeeService.createEmployee("cashier2", "Cashier", "Two", "Cashier", "pass");
        employeeService.createEmployee("admin1", "Admin", "One", "Admin", "pass");

        List<Employee> cashiers = employeeService.getEmployeesByPosition("Cashier");
        assertEquals(2, cashiers.size());

        List<Employee> admins = employeeService.getEmployeesByPosition("Admin");
        assertEquals(1, admins.size());
    }
}

