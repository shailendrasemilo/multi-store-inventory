package com.store.inventory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.store.inventory.entity.Inventory;
import com.store.inventory.entity.Product;
import com.store.inventory.entity.Reservation;
import com.store.inventory.entity.Store;
import com.store.inventory.repository.InventoryRepository;
import com.store.inventory.repository.ProductRepository;
import com.store.inventory.repository.ReservationRepository;
import com.store.inventory.repository.StoreRepository;
import com.store.inventory.service.InventoryService;
import com.store.inventory.service.impl.InventoryServiceImpl;

@SpringBootTest
class MultiStoreInventoryApplicationTests {

	@Mock
	private InventoryRepository inventoryRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private InventoryService inventoryService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testReserveProduct_Success() {
		Store store = new Store();
		store.setStoreId(1L);

		Product product = new Product();
		product.setProductId(1L);

		Inventory inventory = new Inventory();
		inventory.setStore(store);
		inventory.setProduct(product);
		inventory.setCurrentStock(50);

		when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(inventoryRepository.findByStoreAndProduct(store, product)).thenReturn(inventory);

		assertDoesNotThrow(() -> inventoryService.reserveProduct(1L, 1L, 10));
		verify(inventoryRepository, times(1)).save(inventory);
	}

	@Test
	void testReserveProduct_Failure_InsufficientStock() {
		Store store = new Store();
		store.setStoreId(1L);

		Product product = new Product();
		product.setProductId(1L);

		Inventory inventory = new Inventory();
		inventory.setStore(store);
		inventory.setProduct(product);
		inventory.setCurrentStock(5);

		when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(inventoryRepository.findByStoreAndProduct(store, product)).thenReturn(inventory);

		Exception exception = assertThrows(RuntimeException.class, () -> inventoryService.reserveProduct(1L, 1L, 10));
		assertEquals("Insufficient stock", exception.getMessage());
	}

	@Test
	void testClearExpiredReservations() {
		InventoryRepository inventoryRepository = mock(InventoryRepository.class);
		ReservationRepository reservationRepository = mock(ReservationRepository.class);

		Inventory inventory = new Inventory();
		inventory.setCurrentStock(50);

		Reservation expiredReservation = new Reservation();
		expiredReservation.setReservationTime(LocalDateTime.now().minusMinutes(31));
		expiredReservation.setReservedQuantity(10);
		expiredReservation.setStore(inventory.getStore());
		expiredReservation.setProduct(inventory.getProduct());

		when(reservationRepository.findAll()).thenReturn(List.of(expiredReservation));
		when(inventoryRepository.findByStoreAndProduct(any(), any())).thenReturn(inventory);

		InventoryService service = new InventoryServiceImpl(inventoryRepository, reservationRepository);
		service.clearExpiredReservations();

		verify(reservationRepository, times(1)).delete(expiredReservation);
		verify(inventoryRepository, times(1)).save(inventory);
		assert inventory.getCurrentStock() == 60;
	}
}
