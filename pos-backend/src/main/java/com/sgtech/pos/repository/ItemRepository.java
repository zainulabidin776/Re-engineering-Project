package com.sgtech.pos.repository;

import com.sgtech.pos.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByItemId(Integer itemId);
    List<Item> findByQuantityGreaterThan(int minQuantity);
    List<Item> findByQuantityLessThanEqual(int maxQuantity);
    List<Item> findByNameContainingIgnoreCase(String name);
}

