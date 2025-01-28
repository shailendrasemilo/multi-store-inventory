package com.store.inventory.service;

import java.util.List;

import com.store.inventory.entity.Inventory;

public interface InventoryService {
	List<Inventory> getInventoryForStore(Long storeId);

	void reserveProduct(Long storeId, Long productId, int quantity);

	void transferStock(Long fromStoreId, Long toStoreId, Long productId, int quantity);

	void checkAndSendStockAlerts();

	void clearExpiredReservations();
}
