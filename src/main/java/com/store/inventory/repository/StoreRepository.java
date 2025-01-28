package com.store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.store.inventory.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

}
