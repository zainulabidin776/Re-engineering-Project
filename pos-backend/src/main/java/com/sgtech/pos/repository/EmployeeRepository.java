package com.sgtech.pos.repository;

import com.sgtech.pos.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUsername(String username);
    List<Employee> findByPosition(String position);
    boolean existsByUsername(String username);
}

