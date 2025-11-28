package com.sgtech.pos.repository;

import com.sgtech.pos.model.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RentalItemRepository extends JpaRepository<RentalItem, UUID> {
}

