package com.store.inventory.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Reservation {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long reservationId;

	    @ManyToOne
	    @JoinColumn(name = "storeId", nullable = false)
	    private Store store;

	    @ManyToOne
	    @JoinColumn(name = "productId", nullable = false)
	    private Product product;

	    private int reservedQuantity;
	    private LocalDateTime reservationTime;
}
