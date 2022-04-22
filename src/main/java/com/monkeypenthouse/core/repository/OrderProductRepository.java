package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.entity.OrderProduct;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long>{
}
