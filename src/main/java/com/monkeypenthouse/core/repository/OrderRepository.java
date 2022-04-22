package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);
}
