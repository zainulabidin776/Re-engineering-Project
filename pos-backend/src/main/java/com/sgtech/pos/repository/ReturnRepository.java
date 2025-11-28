package com.sgtech.pos.repository;

import com.sgtech.pos.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReturnRepository extends JpaRepository<Return, UUID> {
}

