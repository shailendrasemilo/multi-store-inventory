package com.store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.store.inventory.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>{

}
