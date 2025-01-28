package com.store.inventory.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.store.inventory.entity.Inventory;
import com.store.inventory.entity.Product;
import com.store.inventory.entity.Reservation;
import com.store.inventory.entity.Store;
import com.store.inventory.repository.InventoryRepository;
import com.store.inventory.repository.ProductRepository;
import com.store.inventory.repository.ReservationRepository;
import com.store.inventory.repository.StoreRepository;
import com.store.inventory.service.InventoryService;

import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {
	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private ProductRepository productRepository;

	public InventoryServiceImpl(InventoryRepository inventoryRepository2,
			ReservationRepository reservationRepository2) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Inventory> getInventoryForStore(Long storeId) {
		return inventoryRepository.findAll();
	}

	@Override
	@Transactional
	public void reserveProduct(Long storeId, Long productId, int quantity) {
		Store store = storeRepository.findById(storeId).orElseThrow();
		Product product = productRepository.findById(productId).orElseThrow();
		Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product);

		if (inventory.getCurrentStock() < quantity) {
			throw new RuntimeException("Insufficient stock");
		}

		inventory.setCurrentStock(inventory.getCurrentStock() - quantity);
		inventoryRepository.save(inventory);

		Reservation reservation = new Reservation();
		reservation.setStore(store);
		reservation.setProduct(product);
		reservation.setReservedQuantity(quantity);
		reservation.setReservationTime(LocalDateTime.now());
		reservationRepository.save(reservation);
	}

	@Override
	@Transactional
	public void transferStock(Long fromStoreId, Long toStoreId, Long productId, int quantity) {
		Store fromStore = storeRepository.findById(fromStoreId).orElseThrow();
		Store toStore = storeRepository.findById(toStoreId).orElseThrow();
		Product product = productRepository.findById(productId).orElseThrow();

		Inventory fromInventory = inventoryRepository.findByStoreAndProduct(fromStore, product);
		Inventory toInventory = inventoryRepository.findByStoreAndProduct(toStore, product);

		if (fromInventory.getCurrentStock() < quantity) {
			throw new RuntimeException("Insufficient stock in source store");
		}

		fromInventory.setCurrentStock(fromInventory.getCurrentStock() - quantity);
		toInventory.setCurrentStock(toInventory.getCurrentStock() + quantity);

		inventoryRepository.save(fromInventory);
		inventoryRepository.save(toInventory);
	}

	@Override
	public void checkAndSendStockAlerts() {
		List<Inventory> inventories = inventoryRepository.findAll();
		for (Inventory inventory : inventories) {
			double threshold = inventory.getProduct().getCapacity() * 0.2;
			if (inventory.getCurrentStock() < threshold) {
				String alertMessage = String.format(
						"Stock alert! Product: %s, Store: %s, Current Stock: %d (Threshold: %d)",
						inventory.getProduct().getName(), inventory.getStore().getName(), inventory.getCurrentStock(),
						(int) threshold);
				System.out.println(alertMessage); // Replace with proper notification logic (email, SMS, etc.)
			}
		}
	}

	@Override
	@Scheduled(fixedRate = 60000) // Runs every minute
	public void clearExpiredReservations() {
		List<Reservation> reservations = reservationRepository.findAll();
		LocalDateTime now = LocalDateTime.now();

		for (Reservation reservation : reservations) {
			if (Duration.between(reservation.getReservationTime(), now).toMinutes() > 30) {
				// Revert stock in inventory
				Inventory inventory = inventoryRepository.findByStoreAndProduct(reservation.getStore(),
						reservation.getProduct());
				inventory.setCurrentStock(inventory.getCurrentStock() + reservation.getReservedQuantity());
				inventoryRepository.save(inventory);

				// Remove expired reservation
				reservationRepository.delete(reservation);
			}
		}
	}
}
