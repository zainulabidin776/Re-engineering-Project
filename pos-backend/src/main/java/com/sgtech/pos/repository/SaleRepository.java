package com.sgtech.pos.repository;

import com.sgtech.pos.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByEmployeeId(UUID employeeId);
}

