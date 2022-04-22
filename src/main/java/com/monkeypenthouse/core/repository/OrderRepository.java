package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
