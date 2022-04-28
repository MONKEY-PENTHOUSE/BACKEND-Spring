package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.entity.Purchase;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {

    Optional<Purchase> findByOrderId(String orderId);
}
