package com.store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.store.inventory.entity.Inventory;
import com.store.inventory.entity.Product;
import com.store.inventory.entity.Store;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	Inventory findByStoreAndProduct(Store store, Product product);
}
