package com.store.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.inventory.entity.Inventory;
import com.store.inventory.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	@GetMapping("/{storeId}")
	public List<Inventory> getInventory(@PathVariable Long storeId) {
		return inventoryService.getInventoryForStore(storeId);
	}

	@PostMapping("/reserve")
	public ResponseEntity<String> reserveProduct(@RequestParam Long storeId, @RequestParam Long productId,
			@RequestParam int quantity) {
		try {
			inventoryService.reserveProduct(storeId, productId, quantity);
			return ResponseEntity.ok("Product reserved successfully");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/transfer")
	public void transferStock(@RequestParam Long fromStoreId, @RequestParam Long toStoreId,
			@RequestParam Long productId, @RequestParam int quantity) {
		inventoryService.transferStock(fromStoreId, toStoreId, productId, quantity);
	}
}
