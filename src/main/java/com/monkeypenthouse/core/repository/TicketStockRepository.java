package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TicketStockRepository extends JpaRepository<TicketStock, Long> {
    List<TicketStock> findAllByTicketIdIn(Set<Long> ticketIds);
}
